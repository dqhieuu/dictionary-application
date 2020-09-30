package edu.uet.hieuhadict.services;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.media.MediaPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DictionaryDefinitionProcessor {

  private DictionaryDefinitionProcessor() {}

  private static MediaPlayer mediaPlayer;

  public static List<Node> processDefinition(String wordDefinition) {
    if (wordDefinition == null) {
      System.out.println("Failed to load word");
      return null;
    }

    List<Node> list = new ArrayList<>();
    Scanner stringScanner = new Scanner(wordDefinition);
    String temp = "";
    boolean firstIdiom = true;
    int idiomCount = 1;
    while (stringScanner.hasNextLine()) {
      temp = stringScanner.nextLine();
      Label textField = new Label("");
      textField.setWrapText(true);

      if (temp.length() <= 0) {
        textField.getStyleClass().addAll("definition-label", "label-unformatted");
      } else {
        String prepend;
        String TTSable;
        switch (temp.charAt(0)) {
          case '!':
            if (firstIdiom) {
              Label idiomLabel = new Label("idiom");
              idiomLabel.getStyleClass().addAll("definition-label", "label-header");
              list.add(idiomLabel);
              firstIdiom = false;
            }
            TTSable = temp.substring(1).trim();

            textField.setOnMouseClicked(
                e -> {
                  try {
                    DictionaryMediaPlayer.playTTS(TTSable, "en");
                  } catch (Exception exception) {
                    exception.printStackTrace();
                  }
                });
            textField.setText(idiomCount + ". " + TTSable);
            textField.getStyleClass().addAll("definition-label", "label-idiom");
            idiomCount++;
            break;
          case '^':
            String text = temp.substring(1).trim();
            if (text.length() == 0) {
              textField.setText("/undefined/");
            } else {
              textField.setText(text);
            }

            textField.getStyleClass().addAll("definition-label", "label-pronunciation");
            break;
          case '*':
            if (!firstIdiom) {
              firstIdiom = true;
              idiomCount = 1;
            }
            textField.setText(temp.substring(1).trim());
            textField.getStyleClass().addAll("definition-label", "label-header");
            break;
          case '-':
            prepend = "• ";
            if (!firstIdiom) {
              prepend = "  •  ";
            }
            textField.setText(prepend + temp.substring(1).trim());

            textField.getStyleClass().addAll("definition-label", "label-explanation");
            break;
          case '=':
            String[] split = temp.split("\\+", 2);
            prepend = "◦ ";
            if (!firstIdiom) {
              prepend = "    ◦  ";
            }
            TTSable = split[0].substring(1).trim();

            textField.setOnMouseClicked(
                e -> {
                  try {
                    DictionaryMediaPlayer.playTTS(TTSable, "en");
                  } catch (Exception exception) {
                    exception.printStackTrace();
                  }
                });
            textField.setText(prepend + TTSable);
            textField.getStyleClass().addAll("definition-label", "label-example");
            if (split.length > 1) {
              prepend = "⮡  ";
              if (!firstIdiom) {
                prepend = "    ⮡  ";
              }
              Label textTranslated = new Label(prepend + split[1].trim());
              textTranslated.getStyleClass().add("label-example-translated");
              textTranslated.setWrapText(true);
              list.add(textField);
              list.add(textTranslated);
              continue;
            }
            break;
          default:
            textField.setText(temp);
            textField.getStyleClass().add("label-unformatted");
            break;
        }
      }
      list.add(textField);
    }
    return list;
  }
}
