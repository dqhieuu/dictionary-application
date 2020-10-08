package edu.uet.hieuhadict;

import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXToggleButton;
import edu.uet.hieuhadict.dao.DatabaseConnection;
import edu.uet.hieuhadict.services.UserPreferences;
import javafx.application.Platform;
import javafx.fxml.FXML;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;


import java.io.File;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.prefs.Preferences;

public class SettingsContentController {
  private final Preferences prefs = UserPreferences.getInstance();

  @FXML JFXToggleButton togglePinWindow;

  @FXML JFXSlider volumeSlider;

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

  @FXML
  private void clearCache() throws SQLException {
    DatabaseConnection.optimize();
    final File tempDir = new File(System.getProperty("java.io.tmpdir"));
    final File[] files = tempDir.listFiles((dir, name) -> name.matches("dict_.*?"));
    if (files != null) {
      Arrays.stream(files).forEach(File::delete);
    }
    System.out.println("Cache cleared.");
  }

  @FXML private void setLanguage(MouseEvent event) {
    System.out.println("Clicked");
    prefs.put(UserPreferences.APP_LANGUAGE, ((ImageView)event.getSource()).getId());
    Platform.exit();
  }

  @FXML
  private void initialize() {
    togglePinWindow.setSelected(
        prefs.getBoolean(UserPreferences.PIN_WINDOW, UserPreferences.DEFAULT_PIN_WINDOW));
    volumeSlider.setValue(prefs.getDouble(UserPreferences.VOLUME, 100.0));
  }
}
