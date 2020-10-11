package edu.uet.hieuhadict.services;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class GoogleService {
  private GoogleService() {}

  // chrome 85 user agent
  private static final String USER_AGENT =
      "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.121 Safari/537.36";

  /**
   * We don't use the official Google API because we have no money, and the unofficial ones are
   * either dead or unreliable af.
   *
   * <p>Returns the translated string got from Google translate. How can we do that for free?
   * Simple. We access the web browser version of the api, which is free, by sending an
   * XMLHttpRequest. Then we parse the retrieved json object.
   *
   * @param text text
   * @param sourceLang locale of the text you want to translate
   * @param destLang locale you want to translate to
   * @return formatted, translated text.
   * @throws Exception exception
   */
  public static String getTranslatedString(String text, String sourceLang, String destLang)
      throws Exception {
    URL u =
        new URL(
            String.format(
                "https://translate.googleapis.com/translate_a/single?client=gtx&sl=%s&tl=%s&dt=t&q=%s",
                sourceLang,
                destLang,
                URLEncoder.encode(text.trim(), StandardCharsets.UTF_8.toString())));
    // https, because why not
    HttpsURLConnection conn = (HttpsURLConnection) u.openConnection();

    StringBuilder result = new StringBuilder();
    try (AutoCloseable ignored = conn::disconnect) {
      // we must always provide user agent when browsing websites nowadays
      conn.setRequestProperty("User-Agent", USER_AGENT);

      if (conn.getResponseCode() == 200) { // successful request
        // try-with-resources
        try (InputStream is = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr)) {
          String lineRead;
          StringBuilder jsonResult = new StringBuilder();
          while ((lineRead = br.readLine()) != null) {
            jsonResult.append(lineRead);
          }

          Gson gson = new Gson();
          /* The json result we get is really weird, so in order to get the wanted translated text,
           * we access the parsed json like this. (we aren't using the official google api, so this is what it has to be) */
          JsonArray arr =
              JsonParser.parseString(jsonResult.toString())
                  .getAsJsonArray()
                  .get(0)
                  .getAsJsonArray();

          // extracts text in json array(s)
          for (int i = 0; i < arr.size(); i++) {
            JsonArray trans = arr.get(i).getAsJsonArray();
            String translated = gson.fromJson(trans.get(0), String.class);
            result.append(translated);
          }
        }
      } else {
        System.out.println("Error while translating text.");
      }
      return result.toString();
    }
  }

  /**
   * We don't use the official Google API because we have no money, and the unofficial ones are
   * either dead or unreliable af.
   *
   * <p>Returns the translated mp3 tts got from Google translate. How can we do that for free?
   * Simple. We access the web browser version of the api, which is free, by sending an
   * XMLHttpRequest. Then we parse the retrieved mp3 binary.
   *
   * @param text text that need to be converted
   * @param lang language locale of text
   * @return A file object that has already been written to temp folder.
   * @throws Exception exception
   */
  public static File getTTSMp3File(String text, String lang) throws Exception {
    URL u =
        new URL(
            String.format(
                "https://translate.google.com.vn/translate_tts?ie=UTF-8&q=%s&tl=%s&client=tw-ob",
                URLEncoder.encode(text.trim(), StandardCharsets.UTF_8.toString()), lang));
    HttpsURLConnection conn = (HttpsURLConnection) u.openConnection();

    File result = null;
    try (AutoCloseable ignored = conn::disconnect) {
      conn.setRequestProperty("User-Agent", USER_AGENT);

      if (conn.getResponseCode() == 200) { // successful request
        result = File.createTempFile("dict_", ".mp3.tmp");
        result.deleteOnExit();
        System.out.println(result.getAbsolutePath());
        // try-with-resources
        try (InputStream is = conn.getInputStream();
            OutputStream os = new FileOutputStream(result)) {
          // writes binary to temp file
          byte[] buffer = new byte[4096];
          int n;
          while ((n = is.read(buffer)) != -1) {
            os.write(buffer, 0, n);
          }
        }

      } else {
        System.out.println("Error while getting .mp3 binary.");
      }
    }
    return result;
  }
}
