package edu.uet.hieuhadict.services;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class DictionaryMediaPlayer {
  private static MediaPlayer mediaPlayer = null;

  public static void playTTS(String content, String locale) throws Exception {
    File speech = GoogleService.getTTSMp3File(content, locale);
    Media mp3 = new Media(speech.toURI().toString());
    mediaPlayer = new MediaPlayer(mp3);
    mediaPlayer.play();
  }

  public static void closePlayer() {
    if (mediaPlayer != null) {
      mediaPlayer.dispose();
    }
  }
}