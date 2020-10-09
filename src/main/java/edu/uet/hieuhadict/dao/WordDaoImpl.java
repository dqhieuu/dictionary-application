package edu.uet.hieuhadict.dao;

import edu.uet.hieuhadict.beans.Dictionary;
import edu.uet.hieuhadict.beans.Word;
import edu.uet.hieuhadict.utils.StringFormat;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/** It just works. Please don't touch it. */
public class WordDaoImpl implements WordDao {
  private static final Connection conn = DatabaseConnection.getConnection();

  @Override
  public List<Dictionary> getAllDictionaries() throws SQLException {
    List<Dictionary> result = new ArrayList<>();
    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT * FROM 'DictList';");
    while (rs.next()) {
      result.add(
          new Dictionary(rs.getString(1), rs.getString(2), rs.getString(3), rs.getBoolean(4)));
    }
    rs.close();
    return result;
  }

  @Override
  public boolean insertDictionary(Dictionary dictionary) throws SQLException {
    String dictName = "dict_" + Instant.now().toEpochMilli();
    String query =
        String.format(
            "CREATE TABLE '%s' (id INTEGER PRIMARY KEY, word TEXT NOT NULL, definition TEXT, is_favorite BOOLEAN DEFAULT (false) NOT NULL);",
            dictName);
    String query2 =
        "INSERT INTO DictList (dictionary, display_name, language_locale, is_enabled) VALUES (?, ?, ?, ?);";
    Statement stmt = conn.createStatement();

    PreparedStatement pstmt = conn.prepareStatement(query2);
    pstmt.setString(1, dictName);
    pstmt.setString(2, dictionary.getDisplayName());
    pstmt.setString(3, dictionary.getLanguageLocale());
    pstmt.setBoolean(4, dictionary.getIsEnabled());

    int affectedRows = stmt.executeUpdate(query);
    affectedRows += pstmt.executeUpdate();
    stmt.close();
    pstmt.close();
    return affectedRows > 0;
  }

  @Override
  public boolean updateDictionary(Dictionary dictionary) throws SQLException {
    String query =
        "UPDATE DictList SET display_name = ?, language_locale = ?, is_enabled = ? WHERE dictionary = ?;";
    PreparedStatement pstmt = conn.prepareStatement(query);
    pstmt.setString(1, dictionary.getDisplayName());
    pstmt.setString(2, dictionary.getLanguageLocale());
    pstmt.setBoolean(3, dictionary.getIsEnabled());
    pstmt.setString(4, dictionary.getDictionary());

    int affectedRows = pstmt.executeUpdate();
    pstmt.close();
    return affectedRows > 0;
  }

  @Override
  public boolean deleteDictionary(Dictionary dictionary) throws SQLException {
    String query = "DELETE FROM DictList WHERE dictionary=?;";
    String query2 = String.format("DROP TABLE '%s';", dictionary.getDictionary());
    PreparedStatement pstmt = conn.prepareStatement(query);
    pstmt.setString(1, dictionary.getDictionary());
    Statement stmt = conn.createStatement();

    int affectedRows = pstmt.executeUpdate();
    affectedRows += stmt.executeUpdate(query2);
    stmt.close();
    pstmt.close();
    return affectedRows > 0;
  }

  @Override
  public Word getWordById(int id, String table) throws SQLException {
    String query = String.format("SELECT * FROM '%s' WHERE id=?;", table);
    Word result;
    try (PreparedStatement pstmt = conn.prepareStatement(query);
        ResultSet rs = pstmt.executeQuery()) {

      pstmt.setString(1, table);
      pstmt.setInt(2, id);

      result = new Word();
      if (rs.first()) {
        result
            .setWord(rs.getString(2))
            .setDefinition(rs.getString(3))
            .setFavorite(rs.getBoolean(4));
      }
    }

    return result;
  }

  @Override
  public List<Word> getAllWordsInDictionary(Dictionary dictionary) throws SQLException {
    List<Word> result = new ArrayList<>();
    if (dictionary == null) {
      return result;
    }
    String query = String.format("SELECT * FROM '%s';", dictionary.getDictionary());

    try (Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query)) {

      while (rs.next()) {
        result.add(new Word(rs.getString(2), rs.getString(3)).setId(rs.getInt(1)));
      }
    }
    return result;
  }

  @Override
  public boolean deleteWordById(int id, String table) throws SQLException {
    String query = String.format("DELETE FROM '%s' WHERE id=?;", table);
    int rowAffected;
    try (PreparedStatement pstmt = conn.prepareStatement(query)) {

      pstmt.setInt(1, id);
      rowAffected = pstmt.executeUpdate();
    }
    return rowAffected > 0;
  }

  /**
   * This function does most of the heavy lifting in this application.
   *
   * <p>It first searches the word accurately; if it doesn't find it, it searches the word
   * approximately using some fuzzy search algorithm. The algorithm is that first it creates a
   * virtual sqlite table, then it uses the table to access Wagner edit distance algorithm underlying
   * that table, given that the dll to hieuspellfix1 has already been loaded. I didn't write the
   * whole function, I just modified some parts of that .c file and compiled it to suit my purposes.
   *
   * <p>However, it currently only accesses the table named table_with_index_0_fuzzy (not really its
   * name), because I don't give a damn about it anymore.
   *
   * @param word string of word
   * @param dictionaries list of dictionaries
   * @return list of words
   * @throws SQLException exception
   */
  @Override
  public List<Word> searchWord(String word, List<Dictionary> dictionaries) throws SQLException {
    List<Word> result = new ArrayList<>();
    if (dictionaries == null || dictionaries.size() == 0) {
      return result;
    }
    StringBuilder query = new StringBuilder();
    query.append("SELECT * FROM (");
    boolean isFirst = true;
    for (Dictionary dict : dictionaries) {
      if (isFirst) {
        isFirst = false;
      } else {
        query.append("UNION ALL ");
      }
      String locale = dict.getLanguageLocale();
      String table = StringFormat.escapeQuotes(dict.getDictionary());

      word = StringFormat.escapeQuotes(word).trim();

      query.append(
          String.format(
              "SELECT *, '%s' AS locale FROM '%s' WHERE word LIKE '%s%%' ", locale, table, word));
    }
    query.append(")ORDER BY LOWER(word) LIMIT 200;");
    try (Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query.toString())) {
      while (rs.next()) {
        result.add(new Word(rs.getString(2), rs.getString(3), rs.getString(5), rs.getBoolean(4)));
      }
    }

    if (result.size() == 0) {
      String locale = dictionaries.get(0).getLanguageLocale();
      String table = StringFormat.escapeQuotes(dictionaries.get(0).getDictionary());
      String fuzzyQuery =
          String.format(
              "SELECT '%s'.*, '%s' AS locale FROM '%sFuzzy' LEFT JOIN '%s' ON '%sFuzzy'.rowid = '%s'.id WHERE '%sFuzzy'.word MATCH '%s';",
              table, locale, table, table, table, table, table, word);
      try (Statement fuzzyStmt = conn.createStatement();
          ResultSet fuzzyRs = fuzzyStmt.executeQuery(fuzzyQuery)) {
        while (fuzzyRs.next()) {
          result.add(
              new Word(
                  fuzzyRs.getString(2),
                  fuzzyRs.getString(3),
                  fuzzyRs.getString(5),
                  fuzzyRs.getBoolean(4)));
        }
      }
    }
    return result;
  }

  @Override
  public boolean insertWord(Word word, String table) throws SQLException {

    String query =
        String.format("INSERT INTO %s (word, definition, is_favorite) VALUES (?, ?, ?);", table);
    int affectedRows;
    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
      pstmt.setString(1, word.getWord());
      pstmt.setString(2, word.getDefinition());
      pstmt.setBoolean(3, word.isFavorite());

      affectedRows = pstmt.executeUpdate();
    }

    return affectedRows > 0;
  }

  @Override
  public boolean updateWordById(Word word, int id, String table) throws SQLException {
    String query =
        String.format(
            "UPDATE %s SET word = ?, definition = ?, is_favorite = ? WHERE id = ?;", table);
    int affectedRows;
    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
      pstmt.setString(1, word.getWord());
      pstmt.setString(2, word.getDefinition());
      pstmt.setBoolean(3, word.isFavorite());
      pstmt.setInt(4, id);
      affectedRows = pstmt.executeUpdate();
    }
    return affectedRows > 0;
  }
}
