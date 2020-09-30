package edu.uet.hieuhadict.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class DatabaseConnection {
  private static Connection conn = null;

  private DatabaseConnection() {}

  public static void connect(String location) {
    try {
      // workaround for jar build
      Class.forName("org.sqlite.JDBC");

      String url = "jdbc:sqlite::resource:" + DatabaseConnection.class.getResource(location);
      conn = DriverManager.getConnection(url);
      System.out.println("Connection to SQLite has been established.");
    } catch (SQLException | ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  public static Connection getConnection() {
    if (conn == null) {
      connect("/database/Dict.db");
    }
    return conn;
  }
}
