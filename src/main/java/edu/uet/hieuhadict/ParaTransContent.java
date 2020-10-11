package edu.uet.hieuhadict;

import com.jfoenix.controls.JFXComboBox;
import edu.uet.hieuhadict.services.DictionaryMediaPlayer;
import edu.uet.hieuhadict.services.GoogleService;
import edu.uet.hieuhadict.services.UserPreferences;
import edu.uet.hieuhadict.utils.LocaleLookup;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class ParaTransContent {
  @FXML private JFXComboBox<String> sourceLanguage;

  @FXML private JFXComboBox<String> destinationLanguage;

  @FXML private TextArea sourceField;

  @FXML private TextArea destinationField;

  private final ResourceBundle langBundle =
      ResourceBundle.getBundle(
          "bundles.Dictionary",
          new Locale(
              UserPreferences.getInstance()
                  .get(UserPreferences.APP_LANGUAGE, UserPreferences.DEFAULT_APP_LANGUAGE)));

  private void loadLanguageToComboBoxes() {
    String[] list =
        LocaleLookup.getLanguageCollection(
            UserPreferences.getInstance()
                .get(UserPreferences.APP_LANGUAGE, UserPreferences.DEFAULT_APP_LANGUAGE));
    sourceLanguage.getItems().addAll(list);
    sourceLanguage.getItems().add(langBundle.getString("auto"));
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
        if (srcIndex >= LocaleLookup.size()) {
          srcLocale = "auto";
        } else {
          srcLocale = LocaleLookup.getLocale(srcIndex);
        }

        String destLocale = LocaleLookup.getLocale(destIndex);

        // Translates in another thread
        Task<Void> task =
            new Task<Void>() {
              @Override
              public Void call() throws Exception {
                String translated =
                    GoogleService.getTranslatedString(textInput, srcLocale, destLocale);
                byte[] toBytes = translated.getBytes();
                translated = new String(toBytes, StandardCharsets.UTF_8);

                destinationField.setText(translated);
                return null;
              }
            };
        new Thread(task).start();
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
  private void saveSrcLocale() {
    int index = sourceLanguage.getSelectionModel().getSelectedIndex();
    if (index != -1) {
      UserPreferences.getInstance().putInt(UserPreferences.PARA_TRANSLATE_SRC, index);
    }
  }

  @FXML
  private void saveDestLocale() {
    int index = destinationLanguage.getSelectionModel().getSelectedIndex();
    if (index != -1) {
      UserPreferences.getInstance().putInt(UserPreferences.PARA_TRANSLATE_DEST, index);
    }
  }

  @FXML
  private void initialize() {
    loadLanguageToComboBoxes();
    Preferences prefs = UserPreferences.getInstance();
    int srcIndex =
        prefs.getInt(
            UserPreferences.PARA_TRANSLATE_SRC, UserPreferences.DEFAULT_PARA_TRANSLATE_SRC);
    int destIndex =
        prefs.getInt(
            UserPreferences.PARA_TRANSLATE_DEST, UserPreferences.DEFAULT_PARA_TRANSLATE_DEST);

    sourceLanguage.getSelectionModel().select(srcIndex);
    destinationLanguage.getSelectionModel().select(destIndex);
  }
}
