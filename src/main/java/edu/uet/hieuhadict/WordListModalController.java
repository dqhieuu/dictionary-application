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

public class WordListModalController {

  @FXML private BorderPane dictionaryModify;

  @FXML private ListView<Word> wordList;

  @FXML private TextField wordName;

  @FXML private TextArea wordDefinition;

  private Dictionary dictionary;

  private WordDao wordDao;

  @FXML
  private void selectWord() {
    Word selected = wordList.getSelectionModel().getSelectedItem();
    if (selected == null) {
      return;
    }

    wordName.setText(selected.getWord());
    wordDefinition.setText(selected.getDefinition());
  }

  @FXML
  private void addWord() throws SQLException {
    wordDao.insertWord(new Word("New word ", ""), dictionary.getDictionary());
    updateWordList();
    wordList.scrollTo(wordList.getItems().size() - 1);
  }

  @FXML
  private void removeWord() throws SQLException {
    Word selected = wordList.getSelectionModel().getSelectedItem();
    if (selected == null) {
      return;
    }

    wordDao.deleteWordById(selected.getId(), dictionary.getDictionary());

    updateWordList();
  }

  @FXML
  private void saveModifiedWord() throws SQLException {
    Word selected = wordList.getSelectionModel().getSelectedItem();
    if (selected == null) {
      return;
    }

    String name = wordName.getText();
    String defintion = wordDefinition.getText();

    if (name.length() == 0) {
      return;
    }

    selected.setWord(name).setDefinition(defintion);

    wordDao.updateWordById(selected, selected.getId(), dictionary.getDictionary());
  }

  @FXML
  private void initialize() {
    wordDao = new WordDaoImpl();
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

  public void setDictionary(Dictionary dictionary) {
    this.dictionary = dictionary;
  }

  public void updateWordList() throws SQLException {
    wordList.getItems().setAll(wordDao.getAllWordsInDictionary(dictionary));
  }
}
