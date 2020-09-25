package edu.uet.hieuhadict.dao;

import java.sql.SQLException;
import java.util.List;

public interface WordDao {
  Word getWord(int id, String table) throws SQLException;
  // boolean insertWord();
  List<Word> searchWord(String word, List<String> tables) throws SQLException;
  // boolean deleteWord();
}
