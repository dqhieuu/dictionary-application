package edu.uet.hieuhadict;

import edu.uet.hieuhadict.services.UserPreferences;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class DictionaryApplication extends Application {
  private double xOffset = 0;
  private double yOffset = 0;

  private final ResourceBundle langBundle =
      ResourceBundle.getBundle(
          "bundles.Dictionary",
          new Locale(
              UserPreferences.getInstance()
                  .get(UserPreferences.APP_LANGUAGE, UserPreferences.DEFAULT_APP_LANGUAGE)));

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws IOException {
    Preferences prefs = UserPreferences.getInstance();
    primaryStage.setTitle("Dictionary Application");

    // loads initial fxml file
    Parent root =
        FXMLLoader.load(getClass().getResource("/fxml/DictionaryApplication.fxml"), langBundle);

    // makes scene draggable
    root.setOnMousePressed(
        event -> {
          xOffset = event.getSceneX();
          yOffset = event.getSceneY();
        });
    root.setOnMouseDragged(
        event -> {
          if (!primaryStage.isMaximized()) {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
          }
        });

    // creates transparent scene and sets scene to fxml node
    Scene scene = new Scene(root, 800, 450);
    scene.setFill(Color.TRANSPARENT);

    // sets css based on preference
    scene
        .getStylesheets()
        .add(
            UserPreferences.getThemePath(
                prefs.getInt(UserPreferences.THEME, UserPreferences.DEFAULT_THEME)));

    // sets pinned window property
    if (prefs.getBoolean(UserPreferences.PIN_WINDOW, false)) {
      primaryStage.setAlwaysOnTop(true);
    }

    // sets transparent stage
    primaryStage.initStyle(StageStyle.TRANSPARENT);
    primaryStage.setScene(scene);

    // shows initial scene
    primaryStage.show();

    // listener that adds rounded class to root corresponding to isMaximized() property
    primaryStage
        .maximizedProperty()
        .addListener(
            (w, o, isMaximized) -> {
              boolean hasRoundedCornersClass = root.getStyleClass().contains("rounded-corners");
              if (isMaximized) {
                if (hasRoundedCornersClass) {
                  root.getStyleClass().remove("rounded-corners");
                }
              } else {
                if (!hasRoundedCornersClass) {
                  root.getStyleClass().add("rounded-corners");
                }
              }
            });
  }
}
