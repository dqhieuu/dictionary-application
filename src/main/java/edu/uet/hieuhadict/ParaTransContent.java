package edu.uet.hieuhadict;

import com.jfoenix.controls.JFXComboBox;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import edu.uet.hieuhadict.services.GoogleService;
import edu.uet.hieuhadict.utils.LocaleLookup;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class ParaTransContent {
  @FXML JFXComboBox<String> sourceLanguage;

  @FXML JFXComboBox<String> destinationLanguage;

  @FXML TextArea sourceField;

  @FXML TextArea destinationField;

  @FXML FontAwesomeIconView srcTTSIcon;

  @FXML FontAwesomeIconView destTTSIcon;

  private void loadLanguageToComboBoxes() {
    String[] list = LocaleLookup.getLanguageCollection("vi");
    for (String elem : list) {
      sourceLanguage.getItems().add(elem);
      destinationLanguage.getItems().add(elem);
    }
  }

  @FXML
  private void translateParagraph() throws Exception {
    int srcIndex = sourceLanguage.getSelectionModel().getSelectedIndex();
    int destIndex = destinationLanguage.getSelectionModel().getSelectedIndex();
    if (srcIndex != -1 && destIndex != -1) {
      String textInput = sourceField.getText().trim();
      if (textInput.length() > 0) {
        String srcLocale = LocaleLookup.getLocale(srcIndex);
        String destLocale = LocaleLookup.getLocale(destIndex);
        String translated = GoogleService.getTranslatedString(textInput, srcLocale, destLocale);
        System.out.println(translated);
        byte[] toBytes = translated.getBytes();
        translated = new String(toBytes, StandardCharsets.UTF_8);

        destinationField.setText(translated);
      }
    }
  }

  @FXML
  private void sourceTTS(MouseEvent e) throws Exception {
    int localeIndex = sourceLanguage.getSelectionModel().getSelectedIndex();
    if (localeIndex != -1) {
      String textInput = sourceField.getText().trim();
      if (textInput.length() > 0) {
        String locale = LocaleLookup.getLocale(localeIndex);
        File speech = GoogleService.getTTSMp3File(textInput, locale);
        Media mp3 = new Media(speech.toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(mp3);
        mediaPlayer.play();
      }
    }
  }

  @FXML
  private void destinationTTS() throws Exception {
    int localeIndex = destinationLanguage.getSelectionModel().getSelectedIndex();
    if (localeIndex != -1) {
      String textInput = destinationField.getText().trim();
      if (textInput.length() > 0) {
        String locale = LocaleLookup.getLocale(localeIndex);
        File speech = GoogleService.getTTSMp3File(textInput, locale);
        Media mp3 = new Media(speech.toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(mp3);
        mediaPlayer.play();
      }
    }
  }

  @FXML
  private void swapLanguages() {
    int srcIndex = sourceLanguage.getSelectionModel().getSelectedIndex();
    int destIndex = destinationLanguage.getSelectionModel().getSelectedIndex();
    sourceLanguage.getSelectionModel().select(destIndex);
    destinationLanguage.getSelectionModel().select(srcIndex);
  }

  @FXML
  private void initialize() {
    loadLanguageToComboBoxes();
    sourceLanguage.getSelectionModel().select(20);
    destinationLanguage.getSelectionModel().select(103);
  }
}
