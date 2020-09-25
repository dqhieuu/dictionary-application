package edu.uet.hieuhadict;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class DictionaryApplication extends Application {
  private double xOffset = 0;
  private double yOffset = 0;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws IOException {
    primaryStage.setTitle("Dictionary Application");

    // loads initial fxml file
    Parent root = FXMLLoader.load(getClass().getResource("/fxml/DictionaryApplication.fxml"));

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
                  root.getStyleClass().clear();
                  root.getStyleClass().addAll("root", "dict-primary-container");
                }
              } else {
                if (!hasRoundedCornersClass) {
                  root.getStyleClass().add("rounded-corners");
                }
              }
            });
  }
}
