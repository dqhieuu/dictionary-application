package edu.uet.hieuhadict.services;

import java.util.prefs.Preferences;

/**
 * A singleton class that has a lot of constants. Used to save the user preferences. Is
 * user-specific on Windows.
 */
public class UserPreferences {
  public static final String PARA_TRANSLATE_SRC = "paraTranslateSrc";
  public static final String PARA_TRANSLATE_DEST = "paraTranslateDest";
  public static final String THEME = "theme";
  public static final String APP_LANGUAGE = "appLanguage";
  public static final String PIN_WINDOW = "pinWindow";
  public static final String VOLUME = "volume";

  public static final String THEME_DEFAULT_CORE = "/css/DictionaryApplication.css";
  public static final String THEME_LIGHT = "/css/ThemeLight.css";
  public static final String THEME_DARK = "/css/ThemeDark.css";
  public static final String THEME_MOE = "/css/ThemeMoe.css";

  public static final int DEFAULT_PARA_TRANSLATE_SRC = 109;
  public static final int DEFAULT_PARA_TRANSLATE_DEST = 103;
  public static final String DEFAULT_APP_LANGUAGE = "vi";
  public static final boolean DEFAULT_PIN_WINDOW = false;
  public static final double DEFAULT_VOLUME = 100.0;
  public static final int DEFAULT_THEME = 0;

  private static final String[] THEME_NAMES = {"hikari", "luminous", "moeta"};
  private static final String[] THEME_PATHS = {THEME_LIGHT, THEME_DARK, THEME_MOE};

  private static final Preferences prefs = Preferences.userNodeForPackage(UserPreferences.class);

  private UserPreferences() {}

  public static Preferences getInstance() {
    return prefs;
  }

  public static String[] getThemeNames() {
    return THEME_NAMES;
  }

  public static String getThemePath(int index) {
    return THEME_PATHS[index];
  }
}
