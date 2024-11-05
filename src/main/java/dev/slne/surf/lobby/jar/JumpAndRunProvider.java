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
  private final Object2ObjectMap<Player, Material> blocks = new Object2ObjectOpenHashMap<>();

  private static final Vector[] OFFSETS = {
      new Vector(3, 0, 0),
      new Vector(-3, 0, 0),
      new Vector(0, 0, 3),
      new Vector(0, 0, -3),
      new Vector(3, 0, 0),
      new Vector(-3, 0, 0),
      new Vector(0, 0, 3),
      new Vector(0, 0, -3),
      new Vector(3, 0, 3),
      new Vector(-3, 0, 3),
      new Vector(3, 0, 3),
      new Vector(-3, 0, 3),
      new Vector(3, 0, 0),
      new Vector(0, 0, 3),
      new Vector(-3, 0, 0)
  };

  private static final Vector[] FALSE_OFFSETS = {
      new Vector(-3, 0, -3),
      new Vector(-3, 0, -1),
      new Vector(-1, 0, -3),
      new Vector(-3, 0, -3),
      new Vector(-1, 0, -3),
      new Vector(-3, 0, -3),
      new Vector(-4, 0, -3),
      new Vector(-3, 0, -4),
      new Vector(-4, 0, -1),
      new Vector(-1, 0, -4),
      new Vector(3, 0, -3),
      new Vector(-3, 0, 3),
      new Vector(3, 0, -3),
      new Vector(-3, 0, 3),
      new Vector(-1, 0, -4)
  };

  public JumpAndRunProvider() {
    this.jumpAndRun = PluginConfig.loadJumpAndRun();
  }

  public void start(Player player) {
    Block[] jumps = new Block[3];
    latestJumps.put(player, jumps);

    this.generateInitialJumps(player);
  }

  private void generateInitialJumps(Player player) {
    Location start = getRandomLocationInRegion(player.getWorld()).add(0, 1, 0);

    Block block = start.getBlock();
    Block next = start.clone().add(OFFSETS[random.nextInt(OFFSETS.length)]).getBlock();
    Block next2 = next.getLocation().clone().add(OFFSETS[random.nextInt(OFFSETS.length)]).getBlock();
    Material material = jumpAndRun.getMaterials().get(random.nextInt(jumpAndRun.getMaterials().size()));

    block.setType(material);
    latestJumps.get(player)[0] = block;

    next.setType(Material.SEA_LANTERN);
    latestJumps.get(player)[1] = next;

    next2.setType(material);
    latestJumps.get(player)[2] = next2;

    player.teleport(block.getLocation().add(0.5, 1, 0.5));

    blocks.put(player, material);
  }

  public void generate(Player player) {
    Block[] jumps = latestJumps.get(player);
    Material material = blocks.get(player);

    if (jumps[0] != null) {
      jumps[0].setType(Material.AIR);
    }

    jumps[0] = jumps[1];
    jumps[1] = jumps[2];

    jumps[1].setType(Material.SEA_LANTERN);

    Location previous = (jumps[1] != null) ? jumps[1].getLocation() : getRandomLocationInRegion(player.getWorld()).add(0, 1, 0);
    int attempts = 0;

    while (attempts < OFFSETS.length) {
      Location location = previous.clone().add(OFFSETS[random.nextInt(OFFSETS.length)]);

      if (this.isInRegion(location) && location.getBlock().getType() == Material.AIR) {
        location.getBlock().setType(material);
        jumps[2] = location.getBlock();
        return;
      }

      attempts++;
    }

    for(Vector vector : FALSE_OFFSETS) {
      Location location = previous.clone().add(vector);

      if (this.isInRegion(location) && location.getBlock().getType() == Material.AIR) {
        location.getBlock().setType(material);
        jumps[2] = location.getBlock();
        return;
      }
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

  public void remove(Player player){
    if(this.getLatestJumps(player) == null){
      return;
    }

    for (Block block : this.getLatestJumps(player)){
      block.setType(Material.AIR);
    }

    this.latestJumps.remove(player);
    player.teleport(jumpAndRun.getSpawn());
  }

  public void removeAll(){
    for(Player player : blocks.keySet()){
      this.remove(player);
    }
  }
}
