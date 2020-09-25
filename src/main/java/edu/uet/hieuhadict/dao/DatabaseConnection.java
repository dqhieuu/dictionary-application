package edu.uet.hieuhadict.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class DatabaseConnection {
  private static Connection conn = null;

  private DatabaseConnection() {}

  public static void connect() {
    try {
      // workaround for jar build
      Class.forName("org.sqlite.JDBC");

      String url =
          "jdbc:sqlite::resource:" + DatabaseConnection.class.getResource("/database/Dict.db");
      conn = DriverManager.getConnection(url);
      System.out.println("Connection to SQLite has been established.");
    } catch (SQLException | ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  public static Connection getConnection() {
    if (conn == null) {
      connect();
    }
    return conn;
  }
}
