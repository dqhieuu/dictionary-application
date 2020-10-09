package edu.uet.hieuhadict.services;

import javafx.scene.Node;
import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DictionaryDefinitionProcessor {

  private DictionaryDefinitionProcessor() {}

  /**
   * This function parses line by line following the <a
   * href="http://www.informatik.uni-leipzig.de/~duc/Dict/install.html">StarDict</a> dictionary
   * format.
   *
   * <p>Line starts with:
   *
   * <ul>
   *   <li>"^": pronunciation
   *   <li>"*": header
   *   <li>"-": standard bulleted line
   *   <li>"!": idiom (numbered line)
   *   <li>"=": (example, translated example) pair of lines, line breaks at the first "+"
   *   <li>None of the above: unformatted line
   * </ul>
   *
   * Nodes are created dynamically, classes are also added to nodes dynamically.
   *
   * @param wordDefinition The definition to process
   * @param locale Language locale of word
   * @return Formatted list of nodes
   */
  public static List<Node> processDefinition(String wordDefinition, String locale) {
    if (wordDefinition == null) {
      System.out.println("Failed to load word");
      return null;
    }

    List<Node> list = new ArrayList<>();
    Scanner stringScanner = new Scanner(wordDefinition);
    String temp;
    boolean firstIdiom = true;
    int idiomCount = 1;
    while (stringScanner.hasNextLine()) {
      temp = stringScanner.nextLine();
      Label textField = new Label("");
      textField.setWrapText(true);

      if (temp.length() <= 0) { // empty line
        textField.getStyleClass().addAll("definition-label", "label-unformatted");
      } else {
        String prepend;
        String TTSable; // text that can be converted to text to speech
        switch (temp.charAt(0)) {
          case '!':
            if (firstIdiom) {
              /*
              If we meet the first idiom, we insert a label with text idiom first.
              This ISN'T arbitrary, the dictionary that originally uses this database
              also processes definitions like this.
               */
              Label idiomLabel = new Label("idiom");
              idiomLabel.getStyleClass().addAll("definition-label", "label-header");
              list.add(idiomLabel);
              firstIdiom = false;
            }
            TTSable = temp.substring(1).trim();

            // adds listener to mouse click event
            textField.setOnMouseClicked(
                e -> {
                  try {
                    DictionaryMediaPlayer.playTTS(TTSable, locale);
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
              // blank definition should display undefined
              textField.setText("/undefined/");
            } else {
              textField.setText(text);
            }

            textField.getStyleClass().addAll("definition-label", "label-pronunciation");
            break;
          case '*':
            // counter for numbered list
            if (!firstIdiom) {
              firstIdiom = true;
              idiomCount = 1;
            }
            textField.setText(temp.substring(1).trim());
            textField.getStyleClass().addAll("definition-label", "label-header");
            break;
          case '-':
            prepend = "• ";
            /* For whatever reason, we double indent when we are in idiom list.
              It's not arbitrary, again.
            */
            if (!firstIdiom) {
              prepend = "  •  ";
            }
            textField.setText(prepend + temp.substring(1).trim());

            textField.getStyleClass().addAll("definition-label", "label-explanation");
            break;
          case '=':
            // detects first "+"
            String[] split = temp.split("\\+", 2);
            prepend = "◦ ";
            // double indention, again
            if (!firstIdiom) {
              prepend = "    ◦  ";
            }
            TTSable = split[0].substring(1).trim();

            // adds listener to mouse click event
            textField.setOnMouseClicked(
                e -> {
                  try {
                    DictionaryMediaPlayer.playTTS(TTSable, locale);
                  } catch (Exception exception) {
                    exception.printStackTrace();
                  }
                });
            textField.setText(prepend + TTSable);
            textField.getStyleClass().addAll("definition-label", "label-example");
            // line split into 2 lines if there are text after "+" character
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
            // line that doesn't match any of the above
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
