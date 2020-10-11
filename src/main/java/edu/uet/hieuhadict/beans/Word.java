package edu.uet.hieuhadict.beans;

/**
 * Word class is a pojo class that has 5 attributes stored in a table representing a dictionary:
 *
 * <ul>
 *   <li>word: The displayed and lookup name of the word. Can be duplicated.
 *   <li>definition: The definition of the corresponding word.
 *   <li>locale: The ISO standard locale code. Used for detecting text-to-speech output language.
 *   <li>isFavorite: If isFavorite=true, Word is displayed in the favorites list. (we have removed
 *       this feature after a debate, but still keep this attribute)
 *   <li>id: Internal attribute that's received from the database. Used only in the WordDao for CRUD
 *       functions.
 * </ul>
 */
public class Word {
  private String word;
  private String definition;
  private String locale;
  private boolean isFavorite;
  private int id = -1;

  public Word() {
    word = "";
    definition = "";
    isFavorite = false;
    locale = "en";
  }

  public Word(String word, String definition) {
    this.word = word;
    this.definition = definition;
  }

  public Word(String word, String definition, String locale, boolean isFavorite) {
    this.word = word;
    this.definition = definition;
    this.locale = locale;
    this.isFavorite = isFavorite;
  }

  public Word(Word other) {
    this.word = other.word;
    this.definition = other.definition;
    this.isFavorite = other.isFavorite;
    this.locale = other.locale;
    this.id = other.id;
  }

  public Word setWord(String word) {
    this.word = word;
    return this;
  }

  public Word setDefinition(String definition) {
    this.definition = definition;
    return this;
  }

  public Word setFavorite(boolean favorite) {
    isFavorite = favorite;
    return this;
  }

  public Word setLocale(String locale) {
    this.locale = locale;
    return this;
  }

  public Word setId(int id) {
    this.id = id;
    return this;
  }

  public String getWord() {
    return word;
  }

  public String getDefinition() {
    return definition;
  }

  public boolean isFavorite() {
    return isFavorite;
  }

  public String getLocale() {
    return locale;
  }

  public int getId() {
    return id;
  }
}
