package dev.slne.surf.lobby.jar.mysql;

import dev.slne.surf.lobby.jar.config.PluginConfig;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.Model;
import org.bukkit.Bukkit;

import java.util.UUID;

public class Database {

  public static void createConnection() {
    try {
      String url = PluginConfig.config().getString("mysql.url");
      String user = PluginConfig.config().getString("mysql.user");
      String password = PluginConfig.config().getString("mysql.password");

      Base.open("com.mysql.jdbc.Driver", url, user, password);

      createTable();
    } catch (Exception e) {
      Bukkit.getConsoleSender().sendMessage( e.getMessage());
    }
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

    try {
      Base.exec(query);
    } catch (Exception e) {
      Bukkit.getConsoleSender().sendMessage(e.getMessage());
    }
  }

  public static void closeConnection() {
    if (Base.hasConnection()) {
      Base.close();
    }
  }

  public static Integer getTrys(UUID uuid) {
    Integer trys = null;

    try {
      Model result = JumpAndRunModel.findFirst("uuid = ?", uuid.toString());
      if (result != null) {
        trys = result.getInteger("trys");
      }

    } catch (Exception e) {
      Bukkit.getConsoleSender().sendMessage(e.getMessage());
    }

    return trys;
  }

  public static Integer getPoints(UUID uuid) {
    Integer points = null;

    try {
      Model result = JumpAndRunModel.findFirst("uuid = ?", uuid.toString());
      if (result != null) {
        points = result.getInteger("points");
      }

    } catch (Exception e) {
      Bukkit.getConsoleSender().sendMessage(e.getMessage());
    }

    return points;
  }

  public static Integer getHighScore(UUID uuid) {
    Integer highScore = null;

    try {
      Model result = JumpAndRunModel.findFirst("uuid = ?", uuid.toString());
      if (result != null) {
        highScore = result.getInteger("high_score");
      }

    } catch (Exception e) {
      Bukkit.getConsoleSender().sendMessage(e.getMessage());
    }

    return highScore;
  }

  public static Boolean getSound(UUID uuid) {
    Boolean sound = true;

    try {
      Model result = JumpAndRunModel.findFirst("uuid = ?", uuid.toString());
      if (result != null) {
        sound = result.getBoolean("sound");
      }

    } catch (Exception e) {
      Bukkit.getConsoleSender().sendMessage(e.getMessage());
    }

    return sound;
  }

  public static void saveSound(UUID uuid, Boolean value) {
    try {
      Model model = JumpAndRunModel.findFirst("uuid = ?", uuid.toString());
      if (model == null) {
        model = new JumpAndRunModel();
        model.set("uuid", uuid.toString());
      }

      model.set("sound", value);
      model.saveIt();
    } catch (Exception e) {
      Bukkit.getConsoleSender().sendMessage(e.getMessage());
    }
  }

  public static void savePoints(UUID uuid, Integer points) {
    try {
      Model model = JumpAndRunModel.findFirst("uuid = ?", uuid.toString());
      if (model == null) {
        model = new JumpAndRunModel();
        model.set("uuid", uuid.toString());
      }

      model.set("points", points);
      model.saveIt();
    } catch (Exception e) {
      Bukkit.getConsoleSender().sendMessage(e.getMessage());
    }
  }

  public static void saveTrys(UUID uuid, Integer trys) {
    try {
      Model model = JumpAndRunModel.findFirst("uuid = ?", uuid.toString());
      if (model == null) {
        model = new JumpAndRunModel();
        model.set("uuid", uuid.toString());
      }

      model.set("trys", trys);
      model.saveIt();
    } catch (Exception e) {
      Bukkit.getConsoleSender().sendMessage(e.getMessage());
    }
  }

  public static void saveHighScore(UUID uuid, Integer highScore) {
    try {
      Model model = JumpAndRunModel.findFirst("uuid = ?", uuid.toString());
      if (model == null) {
        model = new JumpAndRunModel();
        model.set("uuid", uuid.toString());
      }

      model.set("high_score", highScore);
      model.saveIt();
    } catch (Exception e) {
      Bukkit.getConsoleSender().sendMessage(e.getMessage());
    }
  }

  public static Object2ObjectMap<UUID, Integer> getHighScores() {
    Object2ObjectMap<UUID, Integer> highScores = new Object2ObjectOpenHashMap<>();

    try {
      for (Model result : JumpAndRunModel.findAll()) {
        highScores.put(UUID.fromString(result.getString("uuid")), result.getInteger("high_score"));
      }

    } catch (Exception e) {
      Bukkit.getConsoleSender().sendMessage(e.getMessage());
    }
    return highScores;
  }

  public static Object2ObjectMap<UUID, Integer> getPoints() {
    Object2ObjectMap<UUID, Integer> points = new Object2ObjectOpenHashMap<>();
    try {
      for (Model result : JumpAndRunModel.findAll()) {
        points.put(UUID.fromString(result.getString("uuid")), result.getInteger("points"));
      }

    } catch (Exception e) {
      Bukkit.getConsoleSender().sendMessage(e.getMessage());
    }
    return points;
  }
}

class JumpAndRunModel extends Model {
  public JumpAndRunModel() {}
}
