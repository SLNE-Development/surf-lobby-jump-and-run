package dev.slne.surf.lobby.jar;

import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;

import dev.slne.surf.lobby.jar.config.PluginConfig;
import dev.slne.surf.lobby.jar.mysql.Database;
import dev.slne.surf.lobby.jar.util.PluginColor;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import lombok.Getter;
import lombok.experimental.Accessors;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.Sound.Emitter;
import net.kyori.adventure.sound.Sound.Source;
import net.kyori.adventure.text.Component;

import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.security.SecureRandom;

@Getter
@Accessors(fluent = true)
public class JumpAndRunProvider {

  private final JumpAndRun jumpAndRun;
  private final SecureRandom random = new SecureRandom();
  private final ObjectList<Player> awaitingHighScores = new ObjectArrayList<>();
  private final Object2ObjectMap<Player, Block[]> latestJumps = new Object2ObjectOpenHashMap<>();
  private final Object2ObjectMap<Player, Material> blocks = new Object2ObjectOpenHashMap<>();
  private final Object2ObjectMap<Player, Integer> currentPoints = new Object2ObjectOpenHashMap<>();

  private final AsyncLoadingCache<UUID, Integer> points = Caffeine.newBuilder()
      .buildAsync(Database::getPoints);

  private final AsyncLoadingCache<UUID, Integer> highScores = Caffeine.newBuilder()
      .buildAsync(Database::getHighScore);

  private final AsyncLoadingCache<UUID, Integer> trys = Caffeine.newBuilder()
      .buildAsync(Database::getTrys);

  private final AsyncLoadingCache<UUID, Boolean> sounds = Caffeine.newBuilder()
      .buildAsync(Database::getSound);

  private BukkitRunnable runnable;

  /*
  private static final Vector[] OFFSETS = {
      new Vector(3, 0, 3),
      new Vector(-3, 0, 3),
      new Vector(3, 0, -3),
      new Vector(-3, 0, -3),
      new Vector(3, 0, 0),
      new Vector(-3, 0, 0),
      new Vector(0, 0, 3),
      new Vector(0, 0, -3),
      new Vector(4, 0, 0),
      new Vector(-4, 0, 0),
      new Vector(0, 0, 4),
      new Vector(0, 0, -4)
  };
   */

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


  public JumpAndRunProvider() {
    this.jumpAndRun = PluginConfig.loadJumpAndRun();
  }

  public void start(Player player) {
    this.remove(player);

    Block[] jumps = new Block[3];

    this.latestJumps.put(player, jumps);
    this.jumpAndRun.getPlayers().add(player);
    this.currentPoints.put(player, 0);
    this.awaitingHighScores.remove(player);

    this.addTry(player);
    this.generateInitialJumps(player);

    this.queryHighScore(player.getUniqueId()).thenAccept(highScore -> {
      if(highScore == null){
        player.sendMessage(PluginInstance.prefix().append(Component.text("Du bist nun im Parkour. Springe so weit wie möglich, um einen Highscore aufzustellen!")));
        return;
      }

      player.sendMessage(PluginInstance.prefix().append(Component.text(String.format("Du bist nun im Parkour. Springe so weit wie möglich, versuche deinen Highscore von %s zu brechen!", highScore))));
    });
  }



  private void generateInitialJumps(Player player) {
    Location start = getRandomLocationInRegion(player.getWorld()).add(0, 1, 0);
    Block block = start.getBlock();

    Material material = jumpAndRun.getMaterials().get(random.nextInt(jumpAndRun.getMaterials().size()));

    block.setType(material);
    latestJumps.get(player)[0] = block;

    Block next = this.getValidBlock(start, player);

    next.setType(Material.SEA_LANTERN);
    latestJumps.get(player)[1] = next;

    Block next2 = this.getValidBlock(next.getLocation(), player);

    next2.setType(material);
    latestJumps.get(player)[2] = next2;

    player.teleport(block.getLocation().add(0.5, 1, 0.5));
    blocks.put(player, material);
  }



  public void startActionbar(){
    runnable = new BukkitRunnable() {
      @Override
      public void run() {
        jumpAndRun.getPlayers().forEach(player -> {
          player.sendActionBar(Component.text(currentPoints.get(player)).color(PluginColor.BLUE_MID).append(Component.text(" Spr\u00FCnge").color(PluginColor.DARK_GRAY)));
        });
      }
    };
    runnable.runTaskTimerAsynchronously(PluginInstance.instance(), 0L, 20L);
  }

  public void stopActionbar(){
    if(runnable != null && !runnable.isCancelled()){
      runnable.cancel();
    }
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

    Block nextJump = getValidBlock(jumps[1].getLocation(), player);
    nextJump.setType(material);
    jumps[2] = nextJump;
  }

  private Block getValidBlock(Location previousLocation, Player player) {
    int maxAttempts = OFFSETS.length * 2;
    int attempts = 0;

    while (attempts < maxAttempts) {
      int heightOffset = random.nextInt(3) - 1;
      Vector offset = OFFSETS[random.nextInt(OFFSETS.length)];
      Location nextLocation = previousLocation.clone().add(offset).add(0, heightOffset, 0);

      if (!this.isInRegion(nextLocation)) {
        attempts++;
        continue;
      }

      if (nextLocation.getBlock().getType() != Material.AIR ||
          nextLocation.clone().add(0, 1, 0).getBlock().getType() != Material.AIR ||
          nextLocation.clone().add(0, 2, 0).getBlock().getType() != Material.AIR) {
        attempts++;
        continue;
      }

      /* Above the Jump */

      if (latestJumps.get(player)[0] != null && latestJumps.get(player)[0].getLocation().clone().add(0, 1, 0).equals(nextLocation)) {
        attempts++;
        continue;
      }
      if (latestJumps.get(player)[1] != null && latestJumps.get(player)[1].getLocation().clone().add(0, 1, 0).equals(nextLocation)) {
        attempts++;
        continue;
      }
      if (latestJumps.get(player)[2] != null && latestJumps.get(player)[2].getLocation().clone().add(0, 1, 0).equals(nextLocation)) {
        attempts++;
        continue;
      }

      /* 2 Blocks above the Jump */

      if (latestJumps.get(player)[0] != null && latestJumps.get(player)[0].getLocation().clone().add(0, 2, 0).equals(nextLocation)) {
        attempts++;
        continue;
      }
      if (latestJumps.get(player)[1] != null && latestJumps.get(player)[1].getLocation().clone().add(0, 2, 0).equals(nextLocation)) {
        attempts++;
        continue;
      }
      if (latestJumps.get(player)[2] != null && latestJumps.get(player)[2].getLocation().clone().add(0, 2, 0).equals(nextLocation)) {
        attempts++;
        continue;
      }

      if (Math.abs(nextLocation.getY() - previousLocation.getY()) > 1) {
        attempts++;
        continue;
      }

      if (nextLocation.equals(player.getLocation()) || nextLocation.equals(player.getLocation().clone().add(0, 1, 0))) {
        attempts++;
        continue;
      }

      return nextLocation.getBlock();
    }

    player.sendMessage("using extra");
    return previousLocation.clone().add(OFFSETS[0]).getBlock();
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

    int widthX = maxX - minX;
    int heightY = maxY - minY;
    int widthZ = maxZ - minZ;

    if (widthX <= 20 || heightY <= 20 || widthZ <= 20) {
      throw new IllegalStateException("Die Region ist zu klein, sie muss mindestens 20 Blöcke groß sein!");
    }

    minX += 10;
    maxX -= 10;

    minZ += 10;
    maxZ -= 10;

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

  public void remove(Player player) {
    if(this.getLatestJumps(player) == null){
      return;
    }

    for (Block block : this.getLatestJumps(player)) {
      block.setType(Material.AIR);
    }

    if(this.awaitingHighScores.contains(player)){
      this.setHighScore(player);
    }

    this.currentPoints.remove(player);
    this.latestJumps.remove(player);
    this.jumpAndRun.getPlayers().remove(player);

    player.teleport(jumpAndRun.getSpawn());
  }

  public void removeAll() {
    for(Player player : jumpAndRun.getPlayers()){
      this.remove(player);
    }
  }

  public void saveAll() {
    ObjectList<CompletableFuture<Void>> futures = new ObjectArrayList<>();

    for (UUID player : points.synchronous().asMap().keySet()) {
      CompletableFuture<Void> future = savePoints(player);
      futures.add(future);
    }

    for (UUID player : sounds.synchronous().asMap().keySet()) {
      CompletableFuture<Void> future = saveSound(player);
      futures.add(future);
    }

    for (UUID player : highScores.synchronous().asMap().keySet()) {
      CompletableFuture<Void> future = saveHighScore(player);
      futures.add(future);
    }

    for (UUID player : trys.synchronous().asMap().keySet()) {
      CompletableFuture<Void> future = saveTrys(player);
      futures.add(future);
    }

    CompletableFuture<Void> allSaves = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

    allSaves.join();
    this.removeAll();
  }

  public CompletableFuture<Integer> queryTrys(UUID player) {
    return trys.get(player);
  }

  public CompletableFuture<Boolean> querySound(UUID player) {
    return sounds.get(player);
  }

  public CompletableFuture<Integer> queryPoints(UUID player) {
    return points.get(player);
  }

  public CompletableFuture<Integer> queryHighScore(UUID player) {
    return highScores.get(player);
  }

  public CompletableFuture<Void> saveSound(UUID player) {
    return this.querySound(player).thenCompose(sound -> CompletableFuture.runAsync(() -> Database.saveSound(player, sound)).thenRun(() -> this.sounds.synchronous().invalidate(player)));
  }

  public CompletableFuture<Void> saveTrys(UUID player) {
    return this.queryTrys(player).thenCompose(points -> {
      if (points == null) {
        return CompletableFuture.completedFuture(null);
      }

      return CompletableFuture.runAsync(() -> Database.saveTrys(player, points)).thenRun(() -> this.trys.synchronous().invalidate(player));
    });
  }

  public CompletableFuture<Void> savePoints(UUID player) {
    return this.queryPoints(player).thenCompose(points -> {
      if (points == null) {
        return CompletableFuture.completedFuture(null);
      }

      return CompletableFuture.runAsync(() -> Database.savePoints(player, points)).thenRun(() -> this.points.synchronous().invalidate(player));
    });
  }

  public CompletableFuture<Void> saveHighScore(UUID player) {
    return this.queryHighScore(player).thenCompose(highScore -> {
      if (highScore == null) {
        return CompletableFuture.completedFuture(null);
      }

      return CompletableFuture.runAsync(() -> Database.saveHighScore(player, highScore)).thenRun(() -> this.highScores.synchronous().invalidate(player));
    });
  }

  public void setSound(Player player, Boolean value) {
    this.sounds.synchronous().put(player.getUniqueId(), value);
  }


  public void addPoint(Player player) {
    points.get(player.getUniqueId()).thenAccept(points -> {
      int newPoints = (points == null) ? 1 : points + 1;
      this.points.synchronous().put(player.getUniqueId(), newPoints);
    });

    currentPoints.compute(player, (p, curPts) -> curPts == null ? 1 : curPts + 1);

    this.querySound(player.getUniqueId()).thenAccept(sound -> {
      if(!sound){
        player.playSound(Sound.sound(Key.key("entity.experience_orb.pickup"), Source.MASTER, 100f, 1), Emitter.self());
      }
    });
  }

  public void addTry(Player player) {
    this.queryTrys(player.getUniqueId()).thenAccept(trys -> {
      int newTrys = (trys == null) ? 1 : trys + 1;
      this.trys.synchronous().put(player.getUniqueId(), newTrys);
    });
  }

  public void checkHighScore(Player player) {
    Integer currentScore = currentPoints.get(player);

    this.queryHighScore(player.getUniqueId()).thenAccept(highScore -> {
      if (currentScore != null && (highScore == null || currentScore > highScore)) {
        awaitingHighScores.add(player);
      }
    });
  }


  public void setHighScore(Player player) {
    Integer currentScore = currentPoints.get(player);

    this.queryHighScore(player.getUniqueId()).thenAccept(highScore -> {
      if (currentScore != null && (highScore == null || currentScore > highScore)) {
        awaitingHighScores.remove(player);
        highScores.synchronous().put(player.getUniqueId(), currentScore);

        player.sendMessage(PluginInstance.prefix().append(Component.text(String.format("Du hast deinen Highscore gebrochen! Dein neuer Highscore ist %s!", currentScore))));

        this.querySound(player.getUniqueId()).thenAccept(sound -> {
          if(!sound){
            player.playSound(Sound.sound(Key.key("item.totem.use"), Source.MASTER, 100f, 1f), Emitter.self());
          }
        });

        player.showTitle(Title.title(Component.text("Rekord!").color(PluginColor.BLUE_MID), Component.text("Du hast einen neuen persönlichen Rekord aufgestellt.").color(PluginColor.DARK_GRAY), Times.times(
            Duration.of(1, ChronoUnit.SECONDS), Duration.of(2, ChronoUnit.SECONDS), Duration.of(1, ChronoUnit.SECONDS))));
      }
    });
  }


  public void onQuit(Player player) {
    this.saveHighScore(player.getUniqueId());
    this.savePoints(player.getUniqueId());
    this.saveTrys(player.getUniqueId());

    this.currentPoints.remove(player);
    this.awaitingHighScores.remove(player);


    if(this.isJumping(player)) {
      this.remove(player);
    }
  }

  public boolean isJumping(Player player) {
    return this.jumpAndRun.getPlayers().contains(player);
  }
}
