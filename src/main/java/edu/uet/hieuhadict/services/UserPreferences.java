package edu.uet.hieuhadict.services;

import java.util.prefs.Preferences;

public class UserPreferences {
  public static final String PARA_TRANSLATE_SRC = "paraTranslateSrc";
  public static final String PARA_TRANSLATE_DEST = "paraTranslateDest";
  public static final String THEME = "theme";
  public static final String APP_LANGUAGE = "appLanguage";
  public static final String PIN_WINDOW = "pinWindow";
  public static final String VOLUME = "volume";

  public static final int DEFAULT_PARA_TRANSLATE_SRC = 109;
  public static final int DEFAULT_PARA_TRANSLATE_DEST = 103;
  public static final String DEFAULT_APP_LANGUAGE = "vi";
  public static final boolean DEFAULT_PIN_WINDOW = false;
  public static final double DEFAULT_VOLUME = 100.0;

  private static final Preferences prefs = Preferences.userNodeForPackage(UserPreferences.class);

  private UserPreferences() {}

  public static Preferences getInstance() {
    return prefs;
  }
}
