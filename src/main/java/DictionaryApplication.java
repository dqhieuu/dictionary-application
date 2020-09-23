import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
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
  public void start(Stage primaryStage) {
    primaryStage.setTitle("Dictionary Application");
    try {
      // load initial fxml file
      Parent root = FXMLLoader.load(getClass().getResource("/fxml/DictionaryApplication.fxml"));
      // make scene draggable
      root.setOnMousePressed(
          event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
          });
      root.setOnMouseDragged(
          event -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
          });
      Scene scene = new Scene(root, 800, 450);
      scene.setFill(Color.TRANSPARENT);
      primaryStage.initStyle(StageStyle.TRANSPARENT);
      primaryStage.setScene(scene);
      primaryStage.show();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
