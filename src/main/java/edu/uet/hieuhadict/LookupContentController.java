package edu.uet.hieuhadict;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import edu.uet.hieuhadict.beans.Dictionary;
import edu.uet.hieuhadict.beans.Word;
import edu.uet.hieuhadict.dao.WordDao;
import edu.uet.hieuhadict.dao.WordDaoImpl;
import edu.uet.hieuhadict.services.DictionaryDefinitionProcessor;
import edu.uet.hieuhadict.services.DictionaryMediaPlayer;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class LookupContentController {
  @FXML private ListView<Word> wordList;

  @FXML private TextField wordInput;

  @FXML private VBox definitionContent;

  @FXML
  private void updateWordList() throws SQLException {
    WordDao wordDao = new WordDaoImpl();
    List<Dictionary> dicts = wordDao.getAllDictionaries();
    dicts = dicts.stream().filter(Dictionary::getIsEnabled).collect(Collectors.toList());
    if (dicts.size() == 0) return;
    List<Word> words;
    words = wordDao.searchWord(wordInput.getText(), dicts);
    wordList.getItems().setAll(words);
    if (words.size() > 0) {
      wordList.scrollTo(0);
    }
  }

  /** Displays definition by adding list of nodes dynamically */
  @FXML
  private void displayWordContent() {
    Word selectedWord = wordList.getSelectionModel().getSelectedItem();
    if (wordList.getItems().size() > 0) {
      definitionContent.getChildren().clear();
      if (selectedWord == null) {
        selectedWord = wordList.getItems().get(0);
        wordList.getSelectionModel().select(0);
      }
      String dictWord = selectedWord.getWord();
      String wordLocale = selectedWord.getLocale();
      Label wordTitle = new Label(dictWord);
      wordTitle.getStyleClass().add("word-label");
      HBox wordTitleComponent = new HBox();
      wordTitleComponent.setSpacing(8);
      wordTitleComponent.setAlignment(Pos.CENTER_LEFT);
      FontAwesomeIconView wordTTSIcon = new FontAwesomeIconView(FontAwesomeIcon.VOLUME_UP, "22");
      wordTTSIcon.getStyleClass().add("tts-button");
      wordTTSIcon.setOnMouseClicked(
          e -> {
            try {
              DictionaryMediaPlayer.playTTS(dictWord, wordLocale);
            } catch (Exception exception) {
              exception.printStackTrace();
            }
          });
      wordTitleComponent.getChildren().addAll(wordTitle, wordTTSIcon);

      definitionContent.getChildren().add(wordTitleComponent);
      definitionContent
          .getChildren()
          .addAll(
              Objects.requireNonNull(
                  DictionaryDefinitionProcessor.processDefinition(
                      selectedWord.getDefinition(), wordLocale)));
    }
  }

  /**
   * Keys that update the word list.
   *
   * @param e key event
   * @throws SQLException exception
   */
  @FXML
  private void keyReleased(KeyEvent e) throws SQLException {
    KeyCode kc = e.getCode();
    if (kc.isKeypadKey()
        || kc.isDigitKey()
        || kc.isLetterKey()
        || kc == KeyCode.BACK_SPACE
        || kc == KeyCode.DELETE) {
      updateWordList();
    }
  }

  @FXML
  private void initialize() throws SQLException {
    wordList.setCellFactory(
        param ->
            new ListCell<Word>() {
              @Override
              protected void updateItem(Word item, boolean empty) {
                super.updateItem(item, empty);

                setText(empty || item == null || item.getWord() == null ? "" : item.getWord());
              }
            });
    updateWordList();
    displayWordContent();
  }
}
