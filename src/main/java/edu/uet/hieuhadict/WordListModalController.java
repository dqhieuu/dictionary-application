package edu.uet.hieuhadict;

import edu.uet.hieuhadict.beans.Dictionary;
import edu.uet.hieuhadict.beans.Word;
import edu.uet.hieuhadict.dao.WordDao;
import edu.uet.hieuhadict.dao.WordDaoImpl;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class WordListModalController {

  @FXML private BorderPane dictionaryModify;

  @FXML private ListView<Word> wordList;

  private Dictionary dictionary;

  @FXML
  private void initialize() throws SQLException {

    dictionary = (Dictionary) dictionaryModify.getUserData();

    wordList.setCellFactory(
        param ->
            new ListCell<Word>() {
              @Override
              protected void updateItem(Word item, boolean empty) {
                super.updateItem(item, empty);

                setText(empty || item == null || item.getWord() == null ? "" : item.getWord());
              }
            });

    System.out.println(dictionary);

    WordDao wordDao = new WordDaoImpl();
    List<Word> words = wordDao.getAllWordsInDictionary(dictionary);
    wordList.getItems().setAll(words);
  }
}
