package edu.uet.hieuhadict.services;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

/** A static music player class that encapsulates the long procedure to play a music file. */
public class DictionaryMediaPlayer {
  private DictionaryMediaPlayer() {}

  private static MediaPlayer mediaPlayer = null;

  /**
   * Processes and plays tts string immediately after method is called.
   *
   * @param content text to speak
   * @param locale language in which text is spoken
   * @throws Exception exception
   */
  public static void playTTS(String content, String locale) throws Exception {
    File speech = GoogleService.getTTSMp3File(content, locale);
    Media mp3 = new Media(speech.toURI().toString());
    mediaPlayer = new MediaPlayer(mp3);
    double volume =
        UserPreferences.getInstance()
            .getDouble(UserPreferences.VOLUME, UserPreferences.DEFAULT_VOLUME);
    mediaPlayer.setVolume(volume / 100.0);

    /* This method should close the media, then
     * the temp files could be automatically deleted. But it DOESN'T.
     * Consequently, the temp files need to be cleared manually by the user.
     * (this might be a windows 10 problem only)
     * */
    mediaPlayer.setOnEndOfMedia(DictionaryMediaPlayer::closePlayer);
    mediaPlayer.play();
  }

  /** DOES NOT RELEASE FILE(S) NO MATTER WHAT! DUMB JAVAFX! */
  public static void closePlayer() {
    if (mediaPlayer != null) {
      mediaPlayer.stop();
      mediaPlayer.dispose();
      mediaPlayer = null;
    }
  }
}
