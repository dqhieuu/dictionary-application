package edu.uet.hieuhadict;

import com.jfoenix.controls.JFXComboBox;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import edu.uet.hieuhadict.services.DictionaryMediaPlayer;
import edu.uet.hieuhadict.services.GoogleService;
import edu.uet.hieuhadict.utils.LocaleLookup;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.nio.charset.StandardCharsets;

public class ParaTransContent {
  @FXML private JFXComboBox<String> sourceLanguage;

  @FXML private JFXComboBox<String> destinationLanguage;

  @FXML private TextArea sourceField;

  @FXML private TextArea destinationField;

  @FXML private FontAwesomeIconView srcTTSIcon;

  @FXML private FontAwesomeIconView destTTSIcon;

  private void loadLanguageToComboBoxes() {
    String[] list = LocaleLookup.getLanguageCollection("vi");
    sourceLanguage.getItems().addAll(list);
    sourceLanguage.getItems().add("Tự động");
    destinationLanguage.getItems().addAll(list);
  }

  @FXML
  private void translateParagraph() throws Exception {
    int srcIndex = sourceLanguage.getSelectionModel().getSelectedIndex();
    int destIndex = destinationLanguage.getSelectionModel().getSelectedIndex();
    if (srcIndex != -1 && destIndex != -1) {
      String textInput = sourceField.getText().trim();
      if (textInput.length() > 0) {
        String srcLocale;
        System.out.println(srcIndex);
        if (srcIndex >= LocaleLookup.size()) {
          srcLocale = "auto";
        } else {
          srcLocale = LocaleLookup.getLocale(srcIndex);
        }

        String destLocale = LocaleLookup.getLocale(destIndex);
        String translated = GoogleService.getTranslatedString(textInput, srcLocale, destLocale);
        byte[] toBytes = translated.getBytes();
        translated = new String(toBytes, StandardCharsets.UTF_8);

        destinationField.setText(translated);
      }
    }
  }

  @FXML
  private void sourceTTS() throws Exception {
    int localeIndex = sourceLanguage.getSelectionModel().getSelectedIndex();
    if (localeIndex != -1 && localeIndex < LocaleLookup.size()) {
      String textInput = sourceField.getText().trim();
      if (textInput.length() > 0) {
        DictionaryMediaPlayer.playTTS(textInput, LocaleLookup.getLocale(localeIndex));
      }
    }
  }

  @FXML
  private void destinationTTS() throws Exception {
    int localeIndex = destinationLanguage.getSelectionModel().getSelectedIndex();
    if (localeIndex != -1) {
      String textInput = destinationField.getText().trim();
      if (textInput.length() > 0) {
        DictionaryMediaPlayer.playTTS(textInput, LocaleLookup.getLocale(localeIndex));
      }
    }
  }

  @FXML
  private void swapLanguages() {
    int srcIndex = sourceLanguage.getSelectionModel().getSelectedIndex();
    int destIndex = destinationLanguage.getSelectionModel().getSelectedIndex();
    if (srcIndex < LocaleLookup.size()) {
      sourceLanguage.getSelectionModel().select(destIndex);
      destinationLanguage.getSelectionModel().select(srcIndex);
    }
  }

  @FXML
  private void initialize() {
    loadLanguageToComboBoxes();
    sourceLanguage.getSelectionModel().select(109);
    destinationLanguage.getSelectionModel().select(103);
  }
}
