package edu.uet.hieuhadict.dao;

import org.sqlite.SQLiteConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
  private static Connection conn = null;

  private DatabaseConnection() {}

  private static void connect(String location)
      throws SQLException, ClassNotFoundException, IOException {

    // necessary for jar build
    Class.forName("org.sqlite.JDBC");

    SQLiteConfig config = new SQLiteConfig();
    // Loading extension must be enabled
    config.enableLoadExtension(true);

    String url = "jdbc:sqlite::resource:" + DatabaseConnection.class.getResource(location);
    conn = DriverManager.getConnection(url, config.toProperties());

    System.out.println("Connection to SQLite has been established.");
    // Loads the hieuspellfix1 extension
    loadHieuSpellfix1Extension();
  }

  /**
   * Spellfix1 is an extension for SQLite that enables the ability to fuzzy search thanks to a
   * virtual table containing all the needed information based on Levenshtein distance algorithm.
   *
   * <p>This method loads the <b>HieuSpellfix(A|B).dll</b>, which is a precompiled, modified version
   * of the extension that can be loaded on jdbc-sqlite.
   *
   * <p>There are 2 versions of the dll, ver A is for Win64, ver B is for Win32.
   *
   * <p>The dll must be outside of the .jar archive for the jdbc-sqlite to be able to load.
   *
   * <p>Thus, this method checks for the existence of the dll in the temp directory. If it can't
   * find the file, it copies the precompiled dll corresponding to the Windows architecture to the
   * temp folder, then executes the statement to load the dll.
   *
   * <p><i>Note that the jar doesn't include unix and mac dynamic lib, which means the application
   * probably won't start on those OS, since this is one of the core functions of the
   * application.</i>
   *
   * @throws SQLException exception
   * @throws IOException exception
   */
  private static void loadHieuSpellfix1Extension() throws SQLException, IOException {
    String tempDir = System.getProperty("java.io.tmpdir");
    String fileName;
    // 64 or 32 bit
    if (System.getProperty("sun.arch.data.model").equals("64")) {
      fileName = "HieuSpellfixA.dll";
    } else {
      fileName = "HieuSpellfixB.dll";
    }
    Path dllPath = Paths.get(tempDir + fileName);
    if (!Files.exists(dllPath)) {
      Files.copy(
          DatabaseConnection.class.getResourceAsStream("/dll/" + fileName),
          dllPath,
          StandardCopyOption.REPLACE_EXISTING);
      System.out.println(fileName + " extension has been copied to temp folder.");
    }
    try (Statement stmt = conn.createStatement()) {
      stmt.execute(
          String.format("SELECT load_extension('%s');", dllPath.toAbsolutePath().normalize()));
    }
    System.out.println(fileName + " extension has been loaded.");
  }

  /**
   * Executes {@code VACUUM;} statement, an effective command for optimizing disk space taken up by
   * the database.
   *
   * @throws SQLException exception
   */
  public static void optimize() throws SQLException {
    if (conn == null) return;
    try (Statement stmt = conn.createStatement()) {
      stmt.execute("VACUUM;");
    }
  }

  public static Connection getConnection() {
    if (conn == null) {
      try {
        connect("/database/Dict.db");
      } catch (SQLException | ClassNotFoundException | IOException throwables) {
        throwables.printStackTrace();
      }
    }
    return conn;
  }
}
