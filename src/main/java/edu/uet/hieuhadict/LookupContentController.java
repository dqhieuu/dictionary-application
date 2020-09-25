package edu.uet.hieuhadict;

import edu.uet.hieuhadict.dao.Word;
import edu.uet.hieuhadict.dao.WordDao;
import edu.uet.hieuhadict.dao.WordDaoImpl;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class LookupContentController {
    @FXML private ListView<String> wordList;

    @FXML private TextField wordInput;

    @FXML private VBox definitionContent;

    @FXML private void updateWordList() throws SQLException {
        wordList.getItems().clear();
        WordDao wordDao = new WordDaoImpl();
        List<String> tables = new ArrayList<>();
        tables.add("AnhViet");
        List<Word> words;
        words = wordDao.searchWord(wordInput.getText(), tables);
        for(Word word:words) {
            wordList.getItems().add(word.getWord());
        }
    }

    @FXML private void initialize() throws SQLException {
        updateWordList();
    }
}
