package dev.slne.surf.lobby.jar;

import dev.slne.surf.lobby.jar.config.PluginConfig;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.security.SecureRandom;
import lombok.Getter;
import lombok.experimental.Accessors;
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

    Location blockLocation;
    Material type = jumpAndRun.getMaterials().get(random.nextInt(jumpAndRun().getMaterials().size()));

    do {
      int offsetX = random.nextInt(5) - 2;
      int offsetZ = random.nextInt(5) - 2;
      int y = player.getLocation().getBlockY();

      blockLocation = player.getLocation().clone().add(offsetX, y, offsetZ);

    } while (blockLocation.getBlockX() < minX
        || blockLocation.getBlockX() > maxX
        || blockLocation.getBlockY() < minY
        || blockLocation.getBlockY() > maxY
        || blockLocation.getBlockZ() < minZ
        || blockLocation.getBlockZ() > maxZ);

    world.getBlockAt(blockLocation).setType(type);
    jumpAndRun.getLatestBlocks().put(player, world.getBlockAt(blockLocation));
  }
}
