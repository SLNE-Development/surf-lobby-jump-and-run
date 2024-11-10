package dev.slne.surf.lobby.jar.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import dev.slne.surf.lobby.jar.PluginInstance;
import dev.slne.surf.lobby.jar.config.PluginConfig;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

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
    config.setMaxLifetime(600000);

    dataSource = new HikariDataSource(config);
    createTable();
  }

  private static void createTable() {
    String query = """
            CREATE TABLE IF NOT EXISTS jumpandrun (
                uuid VARCHAR(36) NOT NULL PRIMARY KEY,
                points INT DEFAULT 0,
                trys INT DEFAULT 0,
                sound TINYINT(1) DEFAULT TRUE,
                high_score INT DEFAULT 0
            )""";

    try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
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

  public static Integer getTrys(UUID uuid) {
    Integer trys = null;
    String query = "SELECT trys FROM jumpandrun WHERE uuid = ?";

    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setString(1, uuid.toString());
      ResultSet result = statement.executeQuery();

      if (result.next()) {
        trys = result.getInt("trys");
      }
    } catch (SQLException e) {
      Bukkit.getConsoleSender().sendMessage(e.getMessage());
    }

    return trys;
  }

  public static Integer getPoints(UUID uuid) {
    Integer points = null;
    String query = "SELECT points FROM jumpandrun WHERE uuid = ?";

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
    String query = "SELECT high_score FROM jumpandrun WHERE uuid = ?";

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

  public static Boolean getSound(UUID uuid) {
    String query = "SELECT sound FROM jumpandrun WHERE uuid = ?";

    try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setString(1, uuid.toString());
      ResultSet result = statement.executeQuery();

      if (result.next()) {
        return result.getBoolean("sound");
      }
    } catch (SQLException e) {
      Bukkit.getConsoleSender().sendMessage(e.getMessage());
      return true;
    }

    return true;
  }

  public static void saveSound(UUID uuid, Boolean value) {
    String query = "INSERT INTO jumpandrun (uuid, sound) VALUES (?, ?) ON DUPLICATE KEY UPDATE sound = ?";

    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setString(1, uuid.toString());
      statement.setBoolean(2, value);
      statement.setBoolean(3, value);
      statement.executeUpdate();
    } catch (SQLException e) {
      Bukkit.getConsoleSender().sendMessage(e.getMessage());
    }
  }

  public static void savePoints(UUID uuid, Integer points) {
    String query = "INSERT INTO jumpandrun (uuid, points) VALUES (?, ?) ON DUPLICATE KEY UPDATE points = ?";

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

  public static void saveTrys(UUID uuid, Integer trys) {
    String query = "INSERT INTO jumpandrun (uuid, trys) VALUES (?, ?) ON DUPLICATE KEY UPDATE trys = ?";

    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setString(1, uuid.toString());
      statement.setInt(2, trys);
      statement.setInt(3, trys);
      statement.executeUpdate();
    } catch (SQLException e) {
      Bukkit.getConsoleSender().sendMessage(e.getMessage());
    }
  }

  public static void saveHighScore(UUID uuid, Integer highScore) {
    String query = "INSERT INTO jumpandrun (uuid, high_score) VALUES (?, ?) ON DUPLICATE KEY UPDATE high_score = ?";

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

  public static Object2ObjectMap<UUID, Integer> getHighScores() {
    Object2ObjectMap<UUID, Integer> highScores = new Object2ObjectOpenHashMap<>();
    String query = "SELECT uuid, high_score FROM jumpandrun";

    PluginInstance.instance().jumpAndRunProvider().saveAll();

    try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(query);
        ResultSet result = statement.executeQuery()) {

      while (result.next()) {
        UUID uuid = UUID.fromString(result.getString("uuid"));
        Integer highScore = result.getInt("high_score");

        highScores.put(uuid, highScore);
      }

    } catch (SQLException e) {
      Bukkit.getConsoleSender().sendMessage(e.getMessage());
    }

    return highScores;
  }

  public static Object2ObjectMap<UUID, Integer> getPoints() {
    Object2ObjectMap<UUID, Integer> points = new Object2ObjectOpenHashMap<>();
    String query = "SELECT uuid, points FROM jumpandrun";

    PluginInstance.instance().jumpAndRunProvider().saveAll();

    try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(query);
        ResultSet result = statement.executeQuery()) {

      while (result.next()) {
        UUID uuid = UUID.fromString(result.getString("uuid"));
        Integer point = result.getInt("points");

        points.put(uuid, point);
      }

    } catch (SQLException e) {
      Bukkit.getConsoleSender().sendMessage(e.getMessage());
    }

    return points;
  }
}
