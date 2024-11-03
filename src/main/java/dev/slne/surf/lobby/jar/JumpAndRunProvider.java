package dev.slne.surf.lobby.jar;

import dev.slne.surf.lobby.jar.config.PluginConfig;
import dev.slne.surf.lobby.jar.util.PluginColor;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.security.SecureRandom;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

@Getter
@Accessors(fluent = true)
public class JumpAndRunProvider {
  private final JumpAndRun jumpAndRun;

  public JumpAndRunProvider(){
    this.jumpAndRun = JumpAndRun.builder()
        .id("lobby-jar")
        .displayName("Lobby Parkour")
        .posOne(PluginConfig.getLocation("settings.pos1"))
        .posTwo(PluginConfig.getLocation("settings.pos2"))
        .difficulty(PluginConfig.config().getInt("settings.difficulty"))
        .players(new ObjectArrayList<>())
        .materials(ObjectArrayList.of(Material.RED_CONCRETE, Material.REDSTONE_BLOCK, Material.RED_WOOL))
        .latestBlocks(new Object2ObjectOpenHashMap<>())
        .build();
  }

  public void generateBlock(Player player) {
    SecureRandom random = new SecureRandom();
    Location posOne = jumpAndRun.getPosOne();
    Location posTwo = jumpAndRun.getPosTwo();
    World world = player.getWorld();

    int minX = Math.min(posOne.getBlockX(), posTwo.getBlockX());
    int maxX = Math.max(posOne.getBlockX(), posTwo.getBlockX());
    int minY = Math.min(posOne.getBlockY(), posTwo.getBlockY());
    int maxY = Math.max(posOne.getBlockY(), posTwo.getBlockY());
    int minZ = Math.min(posOne.getBlockZ(), posTwo.getBlockZ());
    int maxZ = Math.max(posOne.getBlockZ(), posTwo.getBlockZ());
    int attempts = 0;

    Location playerLocation = player.getLocation();
    ObjectList<Location> locations = new ObjectArrayList<>();

    locations.add(playerLocation.clone().add(playerLocation.add(jumpAndRun.getDifficulty(), 0, jumpAndRun.getDifficulty()).getDirection().setY(0).normalize().multiply(1)));
    locations.add(playerLocation.clone().add(playerLocation.clone().add(jumpAndRun.getDifficulty(), 0, jumpAndRun.getDifficulty()).getDirection().setY(0).normalize().multiply(1).rotateAroundY(Math.toRadians(-45))));
    locations.add(playerLocation.clone().add(playerLocation.clone().add(jumpAndRun.getDifficulty(), 0, jumpAndRun.getDifficulty()).getDirection().setY(0).normalize().multiply(1).rotateAroundY(Math.toRadians(45))));

    while (attempts < 8) {
      Location blockLocation = locations.get(random.nextInt(locations.size()));
      int offsetY = random.nextInt(2) - 1;

      blockLocation.add(0, offsetY, 0);

      if (!blockLocation.getBlock().getType().equals(Material.AIR)) {
        attempts++;
        continue;
      }

      if(blockLocation.equals(playerLocation)){
        attempts++;
        continue;
      }

      Material type = jumpAndRun.getMaterials().get(random.nextInt(jumpAndRun.getMaterials().size()));

      if (blockLocation.getBlockX() >= minX && blockLocation.getBlockX() <= maxX &&
          blockLocation.getBlockY() >= minY && blockLocation.getBlockY() <= maxY &&
          blockLocation.getBlockZ() >= minZ && blockLocation.getBlockZ() <= maxZ) {

        world.getBlockAt(blockLocation).setType(type);

        /*

        if(jumpAndRun.getLatestBlocks().get(player) != null){
          jumpAndRun.getLatestBlocks().get(player).setType(Material.AIR);
        }

        */

        jumpAndRun.getLatestBlocks().put(player, world.getBlockAt(blockLocation));
        return;
      }

      attempts++;
    }

    player.sendMessage(PluginInstance.prefix().append(Component.text("Es ist ein Fehler aufgetreten.").color(PluginColor.RED)));
  }

}
