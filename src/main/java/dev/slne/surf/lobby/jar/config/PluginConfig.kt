package dev.slne.surf.lobby.jar.config;

import dev.slne.surf.lobby.jar.JumpAndRun;
import dev.slne.surf.lobby.jar.PluginInstance;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import it.unimi.dsi.fastutil.objects.ObjectList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

public class PluginConfig {
  public static FileConfiguration config() {
    return PluginInstance.instance().getConfig();
  }

  public static void createConfig() {
    PluginInstance.instance().saveDefaultConfig();
  }

  public static void save(JumpAndRun jumpAndRun) {
    FileConfiguration config = config();
    ObjectList<String> materialNames = new ObjectArrayList<>();
    String path = "settings.arena";

    for (Material material : jumpAndRun.getMaterials()) {
      materialNames.add(material.name());
    }

    config.set(path + "materials", materialNames);
    config.set(path + "displayname", jumpAndRun.getDisplayName());

    saveLocation(path + "posOne", jumpAndRun.getPosOne());
    saveLocation(path + "posTwo", jumpAndRun.getPosTwo());
    saveLocation(path + "spawn", jumpAndRun.getSpawn());
    saveLocation(path + "start", jumpAndRun.getStart());

    PluginInstance.instance().saveConfig();
  }

  public static JumpAndRun loadJumpAndRun() {
    createConfig();

    String path = "settings.arena";
    Location posOne = getLocation(path + "posOne");
    Location posTwo = getLocation(path + "posTwo");
    Location spawn = getLocation(path + "spawn");
    Location start = getLocation(path + "start");
    String displayName = config().getString(path + "displayname", "Parkour");
    ObjectList<Material> materials = new ObjectArrayList<>();
    ObjectList<String> materialNames = new ObjectArrayList<>(config().getStringList(path + "materials"));

    if(materialNames.isEmpty()){
      materialNames.add(Material.BLACKSTONE.toString());
    }

    for (String name : materialNames) {
      materials.add(Material.valueOf(name));
    }

    return JumpAndRun.builder()
        .displayName(displayName)
        .posOne(posOne)
        .posTwo(posTwo)
        .spawn(spawn)
        .start(start)
        .players(new ObjectArrayList<>())
        .materials(new ObjectArrayList<>(materials))
        .latestBlocks(new Object2ObjectOpenHashMap<>())
        .build();
  }

  public static void saveLocation(String path, Location location) {
    config().set(path + ".world", location.getWorld().getName());
    config().set(path + ".x", location.getBlockX());
    config().set(path + ".y", location.getBlockY());
    config().set(path + ".z", location.getBlockZ());
  }

  public static Location getLocation(String path) {
    Location defaultLocation = Bukkit.getWorlds().getFirst().getSpawnLocation();

    String worldName = config().getString(path + ".world", defaultLocation.getWorld().getName());
    int x = config().getInt(path + ".x", defaultLocation.getBlockX());
    int y = config().getInt(path + ".y", defaultLocation.getBlockY());
    int z = config().getInt(path + ".z", defaultLocation.getBlockZ());

    return new Location(Bukkit.getWorld(worldName), x, y, z);
  }
}
