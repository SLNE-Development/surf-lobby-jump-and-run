package dev.slne.surf.lobby.jar.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.slne.surf.lobby.jar.config.PluginConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.bukkit.Bukkit;

public class Database {

  private static HikariDataSource dataSource;

  public static void createConnection() {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(PluginConfig.config().getString("mysql.url"));
    config.setUsername(PluginConfig.config().getString("mysql.user"));
    config.setPassword(PluginConfig.config().getString("mysql.password"));

    config.addDataSourceProperty("cachePrepStmts", "true");
    config.addDataSourceProperty("prepStmtCacheSize", "250");
    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    config.setMaximumPoolSize(10);

    dataSource = new HikariDataSource(config);
    createTable();
  }

  private static void createTable() {
    String query = """
            CREATE TABLE IF NOT EXISTS players (
                uuid VARCHAR(36) NOT NULL PRIMARY KEY,
                points INT DEFAULT 0,
                high_score INT DEFAULT 0
            )""";

    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
      statement.executeUpdate();
    } catch (SQLException e) {
      Bukkit.getConsoleSender().sendMessage(e.getMessage());
    }
  }

  public static void closeConnection() {
    if (dataSource != null && !dataSource.isClosed()) {
      dataSource.close();
    }
  }

  public static Integer getPoints(UUID uuid) {
    Integer points = null;
    String query = "SELECT points FROM players WHERE uuid = ?";

    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setString(1, uuid.toString());
      ResultSet result = statement.executeQuery();

      if (result.next()) {
        points = result.getInt("points");
      }
    } catch (SQLException e) {
      Bukkit.getConsoleSender().sendMessage(e.getMessage());
    }

    return points;
  }

  public static Integer getHighScore(UUID uuid) {
    Integer highScore = null;
    String query = "SELECT high_score FROM players WHERE uuid = ?";

    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setString(1, uuid.toString());
      ResultSet result = statement.executeQuery();

      if (result.next()) {
        highScore = result.getInt("high_score");
      }
    } catch (SQLException e) {
      Bukkit.getConsoleSender().sendMessage(e.getMessage());
    }

    return highScore;
  }

  public static void savePoints(UUID uuid, Integer points) {
    String query = "INSERT INTO players (uuid, points) VALUES (?, ?) ON DUPLICATE KEY UPDATE points = ?";

    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setString(1, uuid.toString());
      statement.setInt(2, points);
      statement.setInt(3, points);
      statement.executeUpdate();
    } catch (SQLException e) {
      Bukkit.getConsoleSender().sendMessage(e.getMessage());
    }
  }

  public static void saveHighScore(UUID uuid, Integer highScore) {
    String query = "INSERT INTO players (uuid, high_score) VALUES (?, ?) ON DUPLICATE KEY UPDATE high_score = ?";

    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setString(1, uuid.toString());
      statement.setInt(2, highScore);
      statement.setInt(3, highScore);
      statement.executeUpdate();
    } catch (SQLException e) {
      Bukkit.getConsoleSender().sendMessage(e.getMessage());
    }
  }
}
