package edu.uet.hieuhadict.dao;

import edu.uet.hieuhadict.beans.Dictionary;
import edu.uet.hieuhadict.beans.Word;

import java.sql.SQLException;
import java.util.List;

public interface WordDao {
  Word getWordById(int id, String table) throws SQLException;

  List<Word> searchWord(String word, List<String> tables) throws SQLException;

  boolean insertWord(Word word, String table) throws SQLException;

  boolean updateWordById(Word word, int id, String table) throws SQLException;

  boolean deleteWordById(int id, String table) throws SQLException;

  List<Dictionary> getAllDictionaries() throws SQLException;

  boolean insertDictionary(Dictionary dictionary) throws SQLException;

  boolean updateDictionary(Dictionary dictionary) throws SQLException;

  boolean deleteDictionary(Dictionary dictionary) throws SQLException;

}
