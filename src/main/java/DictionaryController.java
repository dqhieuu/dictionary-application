import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class DictionaryController {
  @FXML private BorderPane mainContainer;

  @FXML private AnchorPane leftMenuContainer;

  @FXML private VBox leftMenuVBox;

  @FXML private JFXButton menuButtonLookUp;

  @FXML private JFXButton menuButtonHistory;

  @FXML private JFXButton menuButtonFavorites;

  @FXML private JFXButton menuButtonParaTrans;

  @FXML private JFXButton menuButtonDictMan;

  @FXML private JFXButton menuButtonSettings;

  @FXML
  private void setLeftMenuSizeOnMouseEnter() {
    leftMenuVBox.setPrefWidth(250.0);
    menuButtonLookUp.setMinWidth(Region.USE_COMPUTED_SIZE);
    menuButtonHistory.setMinWidth(Region.USE_COMPUTED_SIZE);
    menuButtonFavorites.setMinWidth(Region.USE_COMPUTED_SIZE);
    menuButtonParaTrans.setMinWidth(Region.USE_COMPUTED_SIZE);
    menuButtonDictMan.setMinWidth(Region.USE_COMPUTED_SIZE);
    menuButtonSettings.setMinWidth(Region.USE_COMPUTED_SIZE);
  }

  @FXML
  private void setLeftMenuSizeOnMouseLeave() {
    leftMenuVBox.setPrefWidth(50.0);
    menuButtonLookUp.setMinWidth(0);
    menuButtonHistory.setMinWidth(0);
    menuButtonFavorites.setMinWidth(0);
    menuButtonParaTrans.setMinWidth(0);
    menuButtonDictMan.setMinWidth(0);
    menuButtonSettings.setMinWidth(0);
  }

  @FXML
  private void initialize() {

  }
}
