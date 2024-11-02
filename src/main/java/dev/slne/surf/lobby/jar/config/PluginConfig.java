package dev.slne.surf.lobby.jar.config;

import dev.slne.surf.lobby.jar.PluginInstance;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

public class PluginConfig {
  public static FileConfiguration config() {
    return PluginInstance.instance().getConfig();
  }

  public static void createConfig() {
    PluginInstance.instance().saveDefaultConfig();
  }

  public static void saveLocationWithSaving(String path, Location location) {
    config().set(path + ".world", location.getWorld());
    config().set(path + ".x", location.getBlockX());
    config().set(path + ".y", location.getBlockY());
    config().set(path + ".z", location.getBlockZ());

    PluginInstance.instance().saveConfig();
  }

  public static void saveLocation(String path, Location location) {
    config().set(path + ".world", location.getWorld());
    config().set(path + ".x", location.getBlockX());
    config().set(path + ".y", location.getBlockY());
    config().set(path + ".z", location.getBlockZ());
  }

  public static Location getLocation(String path) {
    String world = config().getString(path + ".world", Bukkit.getWorlds().getFirst().getName());
    int x = config().getInt(path + ".x", Bukkit.getWorlds().getFirst().getSpawnLocation().getBlockX());
    int y = config().getInt(path + ".y", Bukkit.getWorlds().getFirst().getSpawnLocation().getBlockY());
    int z = config().getInt(path + ".z", Bukkit.getWorlds().getFirst().getSpawnLocation().getBlockZ());

    return new Location(Bukkit.getWorld(world), x, y, z);
  }
}
