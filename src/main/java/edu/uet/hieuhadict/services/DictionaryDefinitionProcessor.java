package edu.uet.hieuhadict.services;

import edu.uet.hieuhadict.LookupContentController;
import edu.uet.hieuhadict.dao.Word;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import jdk.nashorn.internal.parser.Lexer;

import javax.swing.text.html.ListView;
import java.util.ArrayList;
import java.util.Scanner;

public class DictionaryDefinitionProcessor {
  private Label bigWord;

  private Label header;

  private Label pronun;

  private Label definition;

  private Label example_sentence;

  private Label example_sentence_translated;

  private ArrayList<Label> unformatted = new ArrayList<>();

  public Node processDefinition(Word searchWord) {
    if (searchWord == null) {
      return null;
    }
    VBox defProcess = new VBox();
    bigWord.setText(searchWord.getWord());
    String def = searchWord.getDefinition();
    Scanner stringScanner = new Scanner(def);
    String temp = "";
    int index = 0;
    do {
      temp = stringScanner.nextLine();
      if(temp.equals("")) {
        unformatted.add(new Label(""));
        defProcess.getChildren().add(unformatted.get(index));
        index++;
        continue;
      }
      switch (temp.charAt(0)) {
        case '^':
          pronun.setText(temp.substring(1).trim());
          pronun.getStyleClass().add("Lpronun");
          defProcess.getChildren().add(pronun);
          break;
        case '*':
          header.setText(temp.substring(1).trim());
          header.getStyleClass().add("Lheader");
          defProcess.getChildren().add(header);
          break;
        case '-':
          definition.setText(temp.substring(1).trim());
          definition.getStyleClass().add("Ldefinition");
          defProcess.getChildren().add(definition);
          break;
        case '=':
          for (int i = 0; i < temp.length(); i++) {
            if (Character.compare(temp.charAt(i), '+') == 0) {
              example_sentence.setText(temp.substring(1, i).trim());
              example_sentence_translated.setText(temp.substring(i + 1).trim());
              break;
            }
          }
          example_sentence_translated.getStyleClass().add("Lexample_translated");
          example_sentence.getStyleClass().add("Lexample");
          defProcess.getChildren().addAll(example_sentence, example_sentence_translated);
          break;
        default:
          unformatted.add(new Label(temp));
          unformatted.get(index).getStyleClass().add("Lunformatted");
          defProcess.getChildren().add(unformatted.get(index));
          index++;
          break;
      }
    } while (temp != null);
    return defProcess;
  }
  // TODO implement this !!!
}
