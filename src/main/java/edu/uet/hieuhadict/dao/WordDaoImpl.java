package edu.uet.hieuhadict.dao;

import edu.uet.hieuhadict.beans.Dictionary;
import edu.uet.hieuhadict.beans.Word;
import edu.uet.hieuhadict.utils.StringFormat;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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
    return result;
  }

  @Override
  public boolean insertDictionary(Dictionary dictionary) throws SQLException {
    String dictName = "dict_" + Instant.now().toEpochMilli();
    String query =
        String.format(
            "CREATE TABLE '%s' (id INTEGER PRIMARY KEY, word TEXT NOT NULL, definition TEXT, is_favortite BOOLEAN DEFAULT (false) NOT NULL);",
            dictName);
    String query2 =
        "INSERT INTO DictList (dictionary, display_name, language_locale, is_enabled) VALUES (?, ?, ?, ?);";
    Statement stmt = conn.createStatement();

    PreparedStatement pstmt2 = conn.prepareStatement(query2);
    pstmt2.setString(1, dictName);
    pstmt2.setString(2, dictionary.getDisplayName());
    pstmt2.setString(3, dictionary.getLanguageLocale());
    pstmt2.setBoolean(4, dictionary.getIsEnabled());

    int affectedRows = stmt.executeUpdate(query);
    affectedRows += pstmt2.executeUpdate();
    System.out.println(query);
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
    return affectedRows > 0;
  }

  @Override
  public Word getWordById(int id, String table) throws SQLException {
    String query = String.format("SELECT * FROM '%s' WHERE id=?;", table);
    PreparedStatement pstmt = conn.prepareStatement(query);
    pstmt.setString(1, table);
    pstmt.setInt(2, id);
    ResultSet rs = pstmt.executeQuery();
    Word result = new Word();
    if (rs.first()) {
      result.setWord(rs.getString(2)).setDefinition(rs.getString(3)).setFavorite(rs.getBoolean(4));
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

    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery(query);
    while (rs.next()) {
      result.add(new Word(rs.getString(2), rs.getString(3)).setId(rs.getInt(1)));
    }
    return result;
  }

  @Override
  public boolean deleteWordById(int id, String table) throws SQLException {
    String query = String.format("DELETE * FROM '%s' WHERE id=?;", table);
    PreparedStatement pstmt = conn.prepareStatement(query);
    pstmt.setString(1, table);
    pstmt.setInt(2, id);
    int rowAffected = pstmt.executeUpdate();
    return rowAffected > 0;
  }

  @Override
  public List<Word> searchWord(String word, List<Dictionary> dictionaries) throws SQLException {
    List<Word> result = new ArrayList<>();
    if (dictionaries == null || dictionaries.size() == 0) {
      return result;
    }
    StringBuilder query = new StringBuilder();
    boolean isFirst = true;
    for (Dictionary dict : dictionaries) {
      if (isFirst) {
        isFirst = false;
      } else {
        query.append("UNION ALL ");
      }
      String locale = dict.getLanguageLocale();
      String table = StringFormat.escapeQuotes(dict.getDictionary());

      word = StringFormat.escapeQuotes(word);
      word = StringFormat.removeAccent(word);
      query.append(
          String.format(
              "SELECT *, '%s' AS locale FROM '%s' WHERE word LIKE '%s%%' ORDER BY LOWER(word) LIMIT 200",
              locale, table, word));
    }
    query.append(";");
    System.out.println(query);
    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery(query.toString());
    while (rs.next()) {
      result.add(new Word(rs.getString(2), rs.getString(3), rs.getString(5), rs.getBoolean(4)));
    }
    return result;
  }

  @Override
  public boolean insertWord(Word word, String table) throws SQLException {

    String query =
        String.format("INSERT INTO %s (word, definition, is_favorite) VALUES (?, ?, ?);", table);
    PreparedStatement pstmt = conn.prepareStatement(query);
    pstmt.setString(1, word.getWord());
    pstmt.setString(2, word.getDefinition());
    pstmt.setBoolean(3, word.isFavorite());

    int affectedRows = pstmt.executeUpdate();
    return affectedRows > 0;
  }

  @Override
  public boolean updateWordById(Word word, int id, String table) throws SQLException {
    String query =
        String.format(
            "UPDATE %s SET id = ?, word = ?, definition = ?, is_favorite = ? WHERE id = ?;", table);
    PreparedStatement pstmt = conn.prepareStatement(query);
    pstmt.setString(1, word.getWord());
    pstmt.setString(2, word.getDefinition());
    pstmt.setBoolean(3, word.isFavorite());
    pstmt.setInt(4, id);

    int affectedRows = pstmt.executeUpdate();
    return affectedRows > 0;
  }
}
