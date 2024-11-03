package dev.slne.surf.lobby.jar;

import dev.slne.surf.lobby.jar.config.PluginConfig;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.security.SecureRandom;

@Getter
@Accessors(fluent = true)
public class JumpAndRunProvider {

  private final JumpAndRun jumpAndRun;
  private final SecureRandom random = new SecureRandom();
  private final Object2ObjectMap<Player, Block[]> latestJumps = new Object2ObjectOpenHashMap<>();

  private static final Vector[] OFFSETS = {
      new Vector(2, 0, 2),
      new Vector(-2, 0, 2),
      new Vector(3, 0, 1),
      new Vector(1, 0, 3),
      new Vector(-3, 0, 1)
  };

  public JumpAndRunProvider() {
    this.jumpAndRun = JumpAndRun.builder()
        .id("lobby-jar")
        .displayName("Lobby Parkour")
        .posOne(PluginConfig.getLocation("settings.pos1"))
        .posTwo(PluginConfig.getLocation("settings.pos2"))
        .difficulty(PluginConfig.config().getInt("settings.difficulty"))
        .players(new ObjectArrayList<>())
        .materials(ObjectArrayList.of(Material.RED_CONCRETE, Material.RED_WOOL))
        .latestBlocks(new Object2ObjectOpenHashMap<>())
        .build();
  }

  public void start(Player player) {
    Block[] jumps = new Block[3];
    latestJumps.put(player, jumps);

    this.generateInitialJumps(player);
  }

  private void generateInitialJumps(Player player) {
    Location start = getRandomLocationInRegion(player.getWorld()).add(0, 1, 0);
    Block[] jumps = this.getLatestJumps(player);

    for (int i = 0; i < jumps.length; i++) {
      Block block = start.getBlock();

      block.setType(jumpAndRun.getMaterials().get(random.nextInt(jumpAndRun.getMaterials().size())));
      latestJumps.get(player)[i] = block;

      if (i == 0) {
        player.teleport(start.clone().add(0.5, 1, 0.5));
      }
    }
  }

  public void generate(Player player) {
    Block[] jumps = latestJumps.get(player);

    if (jumps[0] != null) {
      jumps[0].setType(Material.AIR);
    }

    jumps[0] = jumps[1];
    jumps[1] = jumps[2];

    Location previous = (jumps[1] != null) ? jumps[1].getLocation() : getRandomLocationInRegion(player.getWorld()).add(0, 1, 0);
    int attempts = 0;

    while (attempts < OFFSETS.length) {
      Location location = previous.clone().add(OFFSETS[random.nextInt(OFFSETS.length)]);
      if (this.isInRegion(location)) {
        location.getBlock().setType(jumpAndRun.getMaterials().get(random.nextInt(jumpAndRun.getMaterials().size())));
        jumps[2] = location.getBlock();
        return;
      }
      attempts++;
    }

    player.sendMessage(PluginInstance.prefix().append(Component.text("Ein Fehler ist aufgetreten.")));
  }

  private Location getRandomLocationInRegion(World world) {
    Location posOne = jumpAndRun.getPosOne();
    Location posTwo = jumpAndRun.getPosTwo();

    int minX = Math.min(posOne.getBlockX(), posTwo.getBlockX());
    int maxX = Math.max(posOne.getBlockX(), posTwo.getBlockX());
    int minY = Math.min(posOne.getBlockY(), posTwo.getBlockY());
    int maxY = Math.max(posOne.getBlockY(), posTwo.getBlockY());
    int minZ = Math.min(posOne.getBlockZ(), posTwo.getBlockZ());
    int maxZ = Math.max(posOne.getBlockZ(), posTwo.getBlockZ());

    int x = random.nextInt(maxX - minX + 1) + minX;
    int y = random.nextInt(maxY - minY + 1) + minY;
    int z = random.nextInt(maxZ - minZ + 1) + minZ;

    return new Location(world, x, y, z);
  }

  public boolean isInRegion(Location location) {
    Location posOne = jumpAndRun.getPosOne();
    Location posTwo = jumpAndRun.getPosTwo();

    if (location.getWorld() != null && posOne.getWorld() != null && posTwo.getWorld() != null) {
      if (!location.getWorld().equals(posOne.getWorld()) || !location.getWorld().equals(posTwo.getWorld())) {
        return false;
      }
    }

    int minX = Math.min(posOne.getBlockX(), posTwo.getBlockX());
    int maxX = Math.max(posOne.getBlockX(), posTwo.getBlockX());
    int minY = Math.min(posOne.getBlockY(), posTwo.getBlockY());
    int maxY = Math.max(posOne.getBlockY(), posTwo.getBlockY());
    int minZ = Math.min(posOne.getBlockZ(), posTwo.getBlockZ());
    int maxZ = Math.max(posOne.getBlockZ(), posTwo.getBlockZ());

    return location.getBlockX() >= minX && location.getBlockX() <= maxX &&
        location.getBlockY() >= minY && location.getBlockY() <= maxY &&
        location.getBlockZ() >= minZ && location.getBlockZ() <= maxZ;
  }

  public Block[] getLatestJumps(Player player) {
    return latestJumps.get(player);
  }
}
