package edu.uet.hieuhadict;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import edu.uet.hieuhadict.beans.Dictionary;
import edu.uet.hieuhadict.beans.Word;
import edu.uet.hieuhadict.dao.WordDao;
import edu.uet.hieuhadict.dao.WordDaoImpl;
import edu.uet.hieuhadict.services.UserPreferences;
import edu.uet.hieuhadict.utils.LocaleLookup;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
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
  @FXML private TableView<Dictionary> dictTable;

  private final WordDao wordDao = new WordDaoImpl();

  @FXML
  private void initialize() throws SQLException {
    TableColumn<Dictionary, String> displayName = new TableColumn<>("Tên từ điển");
    displayName.setCellValueFactory(new PropertyValueFactory<>("displayName"));
    displayName.setResizable(false);
    displayName.setCellFactory(TextFieldTableCell.forTableColumn());
    displayName.setOnEditCommit(
        event -> {
          Dictionary selected = event.getRowValue().setDisplayName(event.getNewValue());
          try {
            wordDao.updateDictionary(selected);
          } catch (SQLException throwables) {
            throwables.printStackTrace();
          }
        });
    displayName.setPrefWidth(250);

    TableColumn<Dictionary, Boolean> isEnabled = new TableColumn<>("Bật");
    isEnabled.setSortable(false);
    isEnabled.setResizable(false);
    isEnabled.setCellValueFactory(new PropertyValueFactory<>("isEnabled"));

    isEnabled.setCellFactory(
        param ->
            new TableCell<Dictionary, Boolean>() {
              final JFXCheckBox checkBox = new JFXCheckBox();

              @Override
              protected void updateItem(Boolean isEnabled, boolean empty) {
                super.updateItem(isEnabled, empty);
                if (isEnabled == null || empty) {
                  setGraphic(null);
                } else {
                  checkBox.setSelected(isEnabled);
                  setGraphic(checkBox);
                  setAlignment(Pos.CENTER);
                }
                checkBox.setOnMouseClicked(
                    e -> {
                      Dictionary selected =
                          (Dictionary)
                              ((TableCell) ((JFXCheckBox) e.getSource()).getParent())
                                  .getTableRow()
                                  .getItem();
                      if (selected == null) {
                        return;
                      }
                      selected.setEnabled(((JFXCheckBox) e.getSource()).isSelected());
                      try {
                        wordDao.updateDictionary(selected);
                      } catch (SQLException throwables) {
                        throwables.printStackTrace();
                      }
                    });
              }
            });

    TableColumn<Dictionary, String> languageLocale = new TableColumn<>("Ngôn ngữ");
    languageLocale.setSortable(false);
    languageLocale.setResizable(false);
    languageLocale.setPrefWidth(200);
    languageLocale.setCellValueFactory(new PropertyValueFactory<>("languageLocale"));

    languageLocale.setCellFactory(
        param ->
            new TableCell<Dictionary, String>() {
              private final JFXComboBox<String> langComboBox = new JFXComboBox<>();
              private final Label label = new Label();

              @Override
              protected void updateItem(String locale, boolean empty) {
                String appLang =
                    UserPreferences.getInstance()
                        .get(UserPreferences.APP_LANGUAGE, UserPreferences.DEFAULT_APP_LANGUAGE);
                super.updateItem(locale, empty);
                if (locale == null || empty) {
                  setGraphic(null);
                } else {
                  langComboBox.getItems().addAll(LocaleLookup.getLanguageCollection(appLang));
                  langComboBox.getSelectionModel().select(LocaleLookup.indexOf(locale));
                  PauseTransition pause = new PauseTransition(Duration.seconds(0.1));
                  langComboBox
                      .showingProperty()
                      .addListener(
                          (obs, oldItem, newItem) -> {
                            if (!newItem) {
                              pause.setOnFinished(
                                  e -> {
                                    Dictionary selected = (Dictionary) getTableRow().getItem();
                                    int langIndex =
                                        langComboBox.getSelectionModel().getSelectedIndex();
                                    selected.setLanguageLocale(LocaleLookup.getLocale(langIndex));
                                    try {
                                      wordDao.updateDictionary(selected);
                                    } catch (SQLException throwables) {
                                      throwables.printStackTrace();
                                    }
                                    setGraphic(label);
                                    label.setText(
                                        Objects.requireNonNull(
                                            LocaleLookup.getLanguageCollection(appLang))[
                                            langComboBox.getSelectionModel().getSelectedIndex()]);
                                  });
                              pause.playFromStart();
                            }
                          });
                  label.setPrefWidth(languageLocale.getPrefWidth());
                  label.setText(
                      Objects.requireNonNull(LocaleLookup.getLanguageCollection(appLang))[
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
    updateDictionaryList();
  }

  private void updateDictionaryList() throws SQLException {
    dictTable.getItems().setAll(wordDao.getAllDictionaries());
  }

  @FXML
  private void addItem() throws SQLException {
    Dictionary newDict = new Dictionary("<DOESN'T MATTER>", "Từ điển mới", "en");
    wordDao.insertDictionary(newDict);

    updateDictionaryList();
  }

  @FXML
  private void removeItem() throws SQLException {
    Dictionary selected = dictTable.getSelectionModel().getSelectedItem();
    if (selected == null) {
      return;
    }
    Alert alert =
        new Alert(
            Alert.AlertType.CONFIRMATION,
            String.format("Bạn có chắc muốn xoá \"%s\" chứ?", selected.getDisplayName()),
            ButtonType.YES,
            ButtonType.NO);
    alert.showAndWait();

    if (alert.getResult() == ButtonType.YES) {
      wordDao.deleteDictionary(selected);

      updateDictionaryList();
    }
  }

  @FXML
  private void editWordList() throws IOException, SQLException {
    Dictionary injection = dictTable.getSelectionModel().getSelectedItem();
    if (injection == null) {
      return;
    }

    ((Stage) dictTable.getScene().getWindow()).setAlwaysOnTop(false);
    UserPreferences.getInstance().putBoolean(UserPreferences.PIN_WINDOW, false);

    Stage stage = new Stage();
    stage.initStyle(StageStyle.UTILITY);
    stage.initModality(Modality.APPLICATION_MODAL);
    stage.setResizable(false);
    stage.setTitle("Dictionary editor");
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/WordListModal.fxml"));
    Scene scene = new Scene(loader.load(), 600, 400);

    WordListModalController controller = loader.getController();
    controller.setDictionary(injection);
    controller.updateWordList();
    stage.setScene(scene);

    stage.show();
  }
}
