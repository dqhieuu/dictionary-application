package edu.uet.hieuhadict;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import edu.uet.hieuhadict.beans.Dictionary;
import edu.uet.hieuhadict.dao.WordDao;
import edu.uet.hieuhadict.dao.WordDaoImpl;
import edu.uet.hieuhadict.utils.LocaleLookup;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.sql.SQLException;

public class DictManContentController {
  @FXML TableView<Dictionary> dictTable;

  @FXML
  private void initialize() throws SQLException {
    TableColumn<Dictionary, String> displayName = new TableColumn<>("Tên từ điển");
    displayName.setCellValueFactory(new PropertyValueFactory<>("displayName"));
    displayName.setCellFactory(TextFieldTableCell.forTableColumn());

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
                  label.setText(
                      LocaleLookup.getLanguageCollection("en")[LocaleLookup.indexOf(locale)]);
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
    dictTable.getItems().add(new Dictionary("Anything", "Từ điển mới", "en"));
  }

  @FXML
  private void removeItem() {
    int selectionIndex = dictTable.getFocusModel().getFocusedIndex();
    if (selectionIndex != -1) {
      dictTable.getItems().remove(selectionIndex);
    }
  }

  @FXML
  private void editWordList() {}

  @FXML
  private void saveDictionaryList() {}
}
