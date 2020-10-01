package edu.uet.hieuhadict;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import edu.uet.hieuhadict.beans.Dictionary;
import edu.uet.hieuhadict.dao.WordDao;
import edu.uet.hieuhadict.dao.WordDaoImpl;
import edu.uet.hieuhadict.utils.LocaleLookup;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

public class DictManContentController {
  @FXML TableView<Dictionary> dictTable;

  @FXML
  private void initialize() throws SQLException {
    TableColumn<Dictionary, String> displayName = new TableColumn<>("Tên từ điển");
    displayName.setCellValueFactory(new PropertyValueFactory<>("displayName"));
    displayName.setCellFactory(TextFieldTableCell.forTableColumn());
    displayName.setPrefWidth(250);

    TableColumn<Dictionary, Boolean> isEnabled = new TableColumn<>("Bật");
    isEnabled.setCellValueFactory(new PropertyValueFactory<>("isEnabled"));

    isEnabled.setCellFactory(
        param ->
            new TableCell<Dictionary, Boolean>() {
              private final JFXCheckBox checkBox = new JFXCheckBox();

              @Override
              protected void updateItem(Boolean isEnabled, boolean empty) {
                super.updateItem(isEnabled, empty);
                if (isEnabled == null || empty) {
                  setGraphic(null);
                } else {
                  checkBox.setSelected(isEnabled);
                  setGraphic(checkBox);
                }
              }
            });

    TableColumn<Dictionary, String> languageLocale = new TableColumn<>("Ngôn ngữ");
    languageLocale.setPrefWidth(200);
    languageLocale.setCellValueFactory(new PropertyValueFactory<>("languageLocale"));

    languageLocale.setCellFactory(
        param ->
            new TableCell<Dictionary, String>() {
              private final JFXComboBox<String> langComboBox = new JFXComboBox<>();
              private final Label label = new Label();

              @Override
              protected void updateItem(String locale, boolean empty) {
                super.updateItem(locale, empty);
                if (locale == null || empty) {
                  setGraphic(null);
                } else {
                  langComboBox.getItems().addAll(LocaleLookup.getLanguageCollection("en"));
                  langComboBox.getSelectionModel().select(LocaleLookup.indexOf(locale));
                  PauseTransition pause = new PauseTransition(Duration.seconds(0.1));
                  langComboBox
                      .showingProperty()
                      .addListener(
                          (obs, oldItem, newItem) -> {
                            if (!newItem) {
                              pause.setOnFinished(
                                  e -> {
                                    setGraphic(label);
                                    label.setText(
                                        Objects.requireNonNull(
                                            LocaleLookup.getLanguageCollection("en"))[
                                            langComboBox.getSelectionModel().getSelectedIndex()]);
                                  });
                              pause.playFromStart();
                            }
                          });
                  label.setPrefWidth(languageLocale.getPrefWidth());
                  label.setText(
                      Objects.requireNonNull(LocaleLookup.getLanguageCollection("en"))[
                          LocaleLookup.indexOf(locale)]);
                  label.setOnMouseClicked(
                      e -> {
                        setGraphic(langComboBox);
                        langComboBox.show();
                      });
                  setGraphic(label);
                }
              }
            });

    dictTable.getColumns().add(isEnabled);
    dictTable.getColumns().add(displayName);
    dictTable.getColumns().add(languageLocale);
    WordDao wordDao = new WordDaoImpl();
    dictTable.getItems().addAll(wordDao.getAllDictionaries());
  }

  @FXML
  private void addItem() {
    /*
    dictTable.getItems().add(new Dictionary("Anything", "Từ điển mới", "en"));
    */
  }

  @FXML
  private void removeItem() {
    /*
    int selectionIndex = dictTable.getFocusModel().getFocusedIndex();
    if (selectionIndex != -1) {
      dictTable.getItems().remove(selectionIndex);
    }
     */
  }

  @FXML
  private void editWordList() throws IOException, SQLException {
    Dictionary injection = dictTable.getSelectionModel().getSelectedItem();
    if (injection == null) {
      return;
    }



    Stage stage = new Stage();
    stage.initStyle(StageStyle.UTILITY);
    stage.initModality(Modality.APPLICATION_MODAL);
    stage.setResizable(false);
    stage.setTitle("Dictionary Modify");
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/WordListModal.fxml"));
    Scene scene = new Scene(loader.load(), 600, 400);

    WordListModalController controller = loader.getController();
    controller.initData(injection);
    stage.setScene(scene);


    stage.show();
  }

  @FXML
  private void saveDictionaryList() {}
}
