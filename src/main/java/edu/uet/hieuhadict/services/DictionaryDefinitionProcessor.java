package edu.uet.hieuhadict.services;

import edu.uet.hieuhadict.dao.Word;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import java.util.Scanner;

public class DictionaryDefinitionProcessor {

  public VBox processDefinition(String wordDefinition) {
    if (wordDefinition == null) {
      System.out.println("Failed to load word");
      return null;
    }
    ArrayList<Label> content = new ArrayList<>();

    VBox defProcess = new VBox();
    Scanner stringScanner = new Scanner(wordDefinition);
    String temp = "";

    int Index = 0;

    do {
      temp = stringScanner.nextLine();
      if (temp.equals("")) {
        content.add(new Label(""));
        defProcess.getChildren().add(content.get(Index));
        Index++;
        continue;
      }
      switch (temp.charAt(0)) {
        case '!':
          content.add(new Label(temp.substring(1).trim()));
          content.get(Index).getStyleClass().add("Lidiom");
          defProcess.getChildren().add(content.get(Index));
          Index++;
          break;
        case '^':
          content.add(new Label(temp.substring(1).trim()) );
          content.get(Index).getStyleClass().add("Lpronun");
          defProcess.getChildren().add(content.get(Index));
          Index++;
          break;
        case '*':
          content.add(new Label(temp.substring(1).trim()) );
          content.get(Index).getStyleClass().add("Lheader");
          defProcess.getChildren().add(content.get(Index));
          Index++;
          break;
        case '-':
          content.add(new Label(temp.substring(1).trim()) );
          content.get(Index).getStyleClass().add("Ldefinition");
          defProcess.getChildren().add(content.get(Index));
          Index++;
          break;
        case '=':
          for (int i = 0; i < temp.length(); i++) {
            if (Character.compare(temp.charAt(i), '+') == 0) {
              content.add(new Label(temp.substring(1, i).trim()) );
              content.add(new Label(temp.substring(i + 1).trim()) );
              break;
            }
          }
          content.get(Index+1).getStyleClass().add("Lexample_translated");
          content.get(Index).getStyleClass().add("Lexample");
          defProcess.getChildren()
                  .addAll(content.get(Index), content.get(Index+1));
          Index = Index+2;
          break;
        default:
          content.add(new Label(temp));
          content.get(Index).getStyleClass().add("Lunformatted");
          defProcess.getChildren().add(content.get(Index));
          Index++;
          break;
      }
    } while (stringScanner.hasNextLine());
    return defProcess;
  }
  // TODO implement this !!!
}
