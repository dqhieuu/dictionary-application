package edu.uet.hieuhadict;

import com.jfoenix.controls.JFXButton;
import edu.uet.hieuhadict.services.DictionaryMediaPlayer;
import edu.uet.hieuhadict.services.UserPreferences;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class DictionaryController {
  private final ResourceBundle langBundle =
      ResourceBundle.getBundle(
          "bundles.Dictionary",
          new Locale(
              UserPreferences.getInstance()
                  .get(UserPreferences.APP_LANGUAGE, UserPreferences.DEFAULT_APP_LANGUAGE)));

  @FXML private BorderPane mainContainer;

  @FXML private AnchorPane leftMenuContainer;

  @FXML private VBox leftMenuVBox;

  @FXML private JFXButton menuButtonLookup;

  @FXML private JFXButton menuButtonHistory;

  @FXML private JFXButton menuButtonFavorites;

  @FXML private JFXButton menuButtonParaTrans;

  @FXML private JFXButton menuButtonDictMan;

  @FXML private JFXButton menuButtonSettings;

  @FXML
  private void setLeftMenuSizeOnMouseEnter() {
    menuButtonLookup.setMinWidth(Region.USE_COMPUTED_SIZE);
    menuButtonHistory.setMinWidth(Region.USE_COMPUTED_SIZE);
    menuButtonFavorites.setMinWidth(Region.USE_COMPUTED_SIZE);
    menuButtonParaTrans.setMinWidth(Region.USE_COMPUTED_SIZE);
    menuButtonDictMan.setMinWidth(Region.USE_COMPUTED_SIZE);
    menuButtonSettings.setMinWidth(Region.USE_COMPUTED_SIZE);
    Timeline timeline =
        new Timeline(
            new KeyFrame(
                Duration.millis(200),
                new KeyValue(leftMenuVBox.prefWidthProperty(), 250, Interpolator.EASE_BOTH)));

    timeline.play();
  }

  @FXML
  private void setLeftMenuSizeOnMouseLeave() {
    menuButtonLookup.setMinWidth(0);
    menuButtonHistory.setMinWidth(0);
    menuButtonFavorites.setMinWidth(0);
    menuButtonParaTrans.setMinWidth(0);
    menuButtonDictMan.setMinWidth(0);
    menuButtonSettings.setMinWidth(0);
    Timeline timeline =
        new Timeline(
            new KeyFrame(
                Duration.millis(200),
                new KeyValue(leftMenuVBox.prefWidthProperty(), 50, Interpolator.EASE_BOTH)));

    timeline.play();
  }

  @FXML
  private void loadLookupContent() throws IOException {
    Parent scene = FXMLLoader.load(getClass().getResource("/fxml/LookupContent.fxml"));
    mainContainer.setCenter(scene);
  }

  @FXML
  private void loadHistoryContent() throws IOException {
    // TODO implement this
  }

  @FXML
  private void loadFavoritesContent() throws IOException {
    // TODO implement this
  }

  @FXML
  private void loadParaTransContent() throws IOException {
    Parent scene =
        FXMLLoader.load(getClass().getResource("/fxml/ParaTransContent.fxml"), langBundle);
    mainContainer.setCenter(scene);
  }

  @FXML
  private void loadDictManContent() throws IOException {
    Parent scene = FXMLLoader.load(getClass().getResource("/fxml/DictManContent.fxml"), langBundle);
    mainContainer.setCenter(scene);
  }

  @FXML
  private void loadSettingsContent() throws IOException {
    Parent scene =
        FXMLLoader.load(getClass().getResource("/fxml/SettingsContent.fxml"), langBundle);
    mainContainer.setCenter(scene);
  }

  @FXML
  private void closeApplication() {
    DictionaryMediaPlayer.closePlayer();
    Platform.exit();
  }

  @FXML
  private void minimizeApplication() {
    ((Stage) mainContainer.getScene().getWindow()).setIconified(true);
  }

  @FXML
  private void restoreOrMaximizeApplication() {
    Stage mainStage = (Stage) mainContainer.getScene().getWindow();
    boolean isMaximized = mainStage.isMaximized();
    mainStage.setMaximized(!isMaximized);
  }

  @FXML
  private void initialize() throws IOException {
    // load initial content
    loadLookupContent();
  }
}
