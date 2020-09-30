package edu.uet.hieuhadict.beans;

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