package edu.uet.hieuhadict.dao;

import edu.uet.hieuhadict.utils.StringFormat;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WordDaoImpl implements WordDao {
  private static final Connection conn = DatabaseConnection.getConnection();

  @Override
  public Word getWord(int id, String table) throws SQLException {
    String query = "SELECT * FROM (?) WHERE id=?;";
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
  public List<Word> searchWord(String word, List<String> tables) throws SQLException {
    StringBuilder query = new StringBuilder();
    boolean isFirst = true;
    for (String table : tables) {
      if (isFirst) {
        isFirst = false;
      } else {
        query.append("UNION ALL ");
      }
      table = StringFormat.escapeQuotes(table);
      word = StringFormat.escapeQuotes(word);
      word = StringFormat.removeAccent(word);
      query.append(
          String.format(
              "SELECT *, '%s' AS 'table' FROM '%s' WHERE word LIKE '%s%%' ORDER BY LOWER(word) LIMIT 1000",
              table, table, word));
    }
    query.append(";");
    System.out.println(query);
    List<Word> result = new ArrayList<>();
    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery(query.toString());
    while (rs.next()) {
      result.add(new Word(rs.getString(2), rs.getString(3), rs.getBoolean(4)));
    }
    return result;
  }
}
