package edu.uet.hieuhadict.dao;

public class Word {
  private String word;
  private String definition;
  private boolean isFavorite;

  Word() {
    word = "";
    definition = "";
    isFavorite = false;
  }

  Word(String word, String definition) {
    this.word = word;
    this.definition = definition;
  }

  Word(String word, String definition, boolean isFavorite) {
    this.word = word;
    this.definition = definition;
    this.isFavorite = isFavorite;
  }

  Word(Word other) {
    this.word = other.word;
    this.definition = other.definition;
    this.isFavorite = other.isFavorite;
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

  public String getWord() {
    return word;
  }

  public String getDefinition() {
    return definition;
  }

  public boolean isFavorite() {
    return isFavorite;
  }
}
