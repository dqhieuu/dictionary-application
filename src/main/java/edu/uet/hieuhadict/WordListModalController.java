package edu.uet.hieuhadict;

import edu.uet.hieuhadict.beans.Dictionary;
import edu.uet.hieuhadict.beans.Word;
import edu.uet.hieuhadict.dao.WordDao;
import edu.uet.hieuhadict.dao.WordDaoImpl;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import java.sql.SQLException;
import java.util.List;

public class WordListModalController {

  @FXML private BorderPane dictionaryModify;

  @FXML private ListView<Word> wordList;

  @FXML private TextField wordName;

  @FXML private TextArea wordDefinition;

  private Dictionary dictionary;

  @FXML private void selectWord() {
    Word selected = wordList.getSelectionModel().getSelectedItem();
    if (selected == null) {
      return;
    }

    wordName.setText(selected.getWord());
    wordDefinition.setText(selected.getDefinition());

  }

  @FXML
  private void initialize() {

    wordList.setCellFactory(
        param ->
            new ListCell<Word>() {
              @Override
              protected void updateItem(Word item, boolean empty) {
                super.updateItem(item, empty);

                setText(empty || item == null || item.getWord() == null ? "" : item.getWord());
              }
            });
  }

  public void initData(Dictionary dictionary) throws SQLException {
    this.dictionary = dictionary;

    WordDao wordDao = new WordDaoImpl();
    List<Word> words = wordDao.getAllWordsInDictionary(dictionary);
    System.out.println(dictionary.getDictionary());
    wordList.getItems().setAll(words);
  }
}
