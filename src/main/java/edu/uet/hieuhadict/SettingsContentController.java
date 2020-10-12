package edu.uet.hieuhadict;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXToggleButton;
import edu.uet.hieuhadict.dao.DatabaseConnection;
import edu.uet.hieuhadict.services.UserPreferences;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.File;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class SettingsContentController {
  private final Preferences prefs = UserPreferences.getInstance();

  private final ResourceBundle langBundle =
      ResourceBundle.getBundle(
          "bundles.Dictionary",
          new Locale(
              UserPreferences.getInstance()
                  .get(UserPreferences.APP_LANGUAGE, UserPreferences.DEFAULT_APP_LANGUAGE)));

  @FXML JFXToggleButton togglePinWindow;

  @FXML JFXSlider volumeSlider;

  @FXML JFXComboBox<String> themeBox;

  @FXML ImageView vnFlagBtn;

  @FXML ImageView usFlagBtn;

  @FXML
  private void pinWindow() {
    Stage primaryStage = (Stage) togglePinWindow.getScene().getWindow();
    primaryStage.setAlwaysOnTop(togglePinWindow.isSelected());
    prefs.putBoolean(UserPreferences.PIN_WINDOW, togglePinWindow.isSelected());
  }

  @FXML
  private void setVolume() {
    prefs.putDouble(UserPreferences.VOLUME, volumeSlider.getValue());
  }

  /** Optimizes database and clear tts temp files. */
  @FXML
  private void clearCache() {
    // clears cache in another thread
    Task<Void> task =
        new Task<Void>() {
          @Override
          public Void call() throws Exception {
            DatabaseConnection.optimize();
            final File tempDir = new File(System.getProperty("java.io.tmpdir"));
            final File[] files = tempDir.listFiles((dir, name) -> name.matches("dict_.*?"));
            if (files != null) {
              Arrays.stream(files).forEach(File::delete);
            }
            System.out.println("Cache cleared.");
            return null;
          }
        };
    new Thread(task).start();
  }

  @FXML
  private void setLanguage(MouseEvent event) {
    // Stops function if setting it to current language
    if (((ImageView) event.getSource()).getStyleClass().contains("active-lang-btn")) {
      return;
    }
    // Removes pin if windows is pinned otherwise dialog gets covered by the app
    ((Stage) togglePinWindow.getScene().getWindow()).setAlwaysOnTop(false);
    Alert alert =
        new Alert(Alert.AlertType.INFORMATION, langBundle.getString("changeLanguageDialog"));

    alert.showAndWait();
    prefs.put(UserPreferences.APP_LANGUAGE, ((ImageView) event.getSource()).getId());
    Platform.exit();
  }

  @FXML
  private void setTheme() {
    int themeIndex = themeBox.getSelectionModel().getSelectedIndex();
    if (themeIndex != -1) {
      Scene scene = themeBox.getScene();
      scene.getStylesheets().setAll(UserPreferences.getThemePath(themeIndex));
      prefs.putInt(UserPreferences.THEME, themeIndex);
    }
  }

  @FXML
  private void initialize() {
    // Sets pinWindow based on preference
    togglePinWindow.setSelected(
        prefs.getBoolean(UserPreferences.PIN_WINDOW, UserPreferences.DEFAULT_PIN_WINDOW));
    // Sets volume based on preference
    volumeSlider.setValue(prefs.getDouble(UserPreferences.VOLUME, 100.0));
    // Loads theme to combobox
    themeBox.getItems().setAll(UserPreferences.getThemeNames());
    themeBox
        .getSelectionModel()
        .select(prefs.getInt(UserPreferences.THEME, UserPreferences.DEFAULT_THEME));

    // Adds class to current language button
    if (prefs
        .get(UserPreferences.APP_LANGUAGE, UserPreferences.DEFAULT_APP_LANGUAGE)
        .equals("vi")) {
      vnFlagBtn.getStyleClass().add("active-lang-btn");
    } else {
      usFlagBtn.getStyleClass().add("active-lang-btn");
    }
  }
}
