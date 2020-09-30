package edu.uet.hieuhadict.beans;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Dictionary {
  private final StringProperty dictionary = new SimpleStringProperty();
  private final StringProperty displayName = new SimpleStringProperty();
  private final StringProperty languageLocale = new SimpleStringProperty();
  private final BooleanProperty isEnabled = new SimpleBooleanProperty();


  public Dictionary(String dictionary, String displayName, String languageLocale) {
    this.dictionary.set(dictionary);
    this.displayName.set(displayName);
    this.languageLocale.set(languageLocale);
    isEnabled.set(true);
  }

  public Dictionary(String dictionary, String displayName, String languageLocale, boolean isEnabled) {
    this.dictionary.set(dictionary);
    this.displayName.set(displayName);
    this.languageLocale.set(languageLocale);
    this.isEnabled.set(isEnabled);
  }

  public Dictionary setDictionary(String dictionary) {
    this.dictionary.set(dictionary);
    return this;
  }

  public Dictionary setDisplayName(String displayName) {
    this.displayName.set(displayName);
    return this;
  }

  public Dictionary setLanguageLocale(String srcLocale) {
    this.languageLocale.set(srcLocale);
    return this;
  }

  public Dictionary setEnabled(boolean enabled) {
    this.isEnabled.set(enabled);
    return this;
  }

  public StringProperty dictionaryProperty() {
    return dictionary;
  }

  public String getDictionary() {
    return dictionary.get();
  }

  public StringProperty displayNameProperty() {
    return displayName;
  }

  public String getDisplayName() {
    return displayName.get();
  }

  public StringProperty languageLocaleProperty() {
    return languageLocale;
  }

  public String getLanguageLocale() {
    return languageLocale.get();
  }

  public BooleanProperty isEnabledProperty() {
    return isEnabled;
  }

  public boolean getIsEnabled() {
    return isEnabled.get();
  }
}
