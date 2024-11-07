package dev.slne.surf.lobby.jar;

import com.github.benmanes.caffeine.cache.AsyncCache;
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
import java.time.temporal.TemporalUnit;
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
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
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

  private final AsyncCache<Player, Integer> points = Caffeine.newBuilder()
      .buildAsync((player) -> Database.getPoints(player.getUniqueId()));

  private final AsyncCache<Player, Integer> highScores = Caffeine.newBuilder()
      .buildAsync((player) -> Database.getPoints(player.getUniqueId()));

  private BukkitRunnable runnable;

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

    this.latestJumps.put(player, jumps);
    this.jumpAndRun.getPlayers().add(player);
    this.currentPoints.put(player, 0);
    this.awaitingHighScores.remove(player);

    this.generateInitialJumps(player);

    this.queryHighScore(player).thenAccept(highScore -> {
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

    player.sendMessage(PluginInstance.prefix().append(Component.text("Ein Fehler ist aufgetreten. Sollte dieser Fehler häufiger auftreten, melde dies bitte in einem Bugreport Ticket!").color(PluginColor.RED)));
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
    for(Player player : blocks.keySet()){
      this.remove(player);
    }
  }

  public void saveAll() {
    for (Player player : Bukkit.getOnlinePlayers()) {
      this.savePoints(player);
      this.saveHighScore(player);
    }
  }

  public CompletableFuture<Integer> queryPoints(Player player) {
    return points.get(player, p -> Database.getPoints(p.getUniqueId()));
  }

  public CompletableFuture<Integer> queryHighScore(Player player) {
    return highScores.get(player, p -> Database.getHighScore(p.getUniqueId()));
  }

  public void savePoints(Player player) {
    this.queryPoints(player).thenAccept(points -> {
      if (points == null) {
        return;
      }

      CompletableFuture.runAsync(() -> Database.savePoints(player.getUniqueId(), points)).thenRun(() -> this.points.synchronous().invalidate(player));
    });
  }

  public void saveHighScore(Player player) {
    this.queryHighScore(player).thenAccept(highScore -> {
      if (highScore == null) {
        return;
      }

      CompletableFuture.runAsync(() -> Database.saveHighScore(player.getUniqueId(), highScore)).thenRun(() -> this.highScores.synchronous().invalidate(player));
    });
  }

  public void addPoint(Player player) {
    points.get(player, p -> Database.getPoints(p.getUniqueId())).thenAccept(points -> {
      int newPoints = (points == null) ? 1 : points + 1;
      this.points.synchronous().put(player, newPoints);
    });

    currentPoints.compute(player, (p, curPts) -> curPts == null ? 1 : curPts + 1);

    player.playSound(Sound.sound(Key.key("block.note_block.bit"), Source.MASTER, 100f, 0), Emitter.self());
  }

  public void checkHighScore(Player player) {
    Integer currentScore = currentPoints.get(player);

    this.queryHighScore(player).thenAccept(highScore -> {
      if (currentScore != null && (highScore == null || currentScore > highScore)) {
        awaitingHighScores.add(player);
      }
    });
  }


  public void setHighScore(Player player) {
    Integer currentScore = currentPoints.get(player);

    this.queryHighScore(player).thenAccept(highScore -> {
      if (currentScore != null && (highScore == null || currentScore > highScore)) {
        awaitingHighScores.remove(player);
        highScores.synchronous().put(player, currentScore);

        player.sendMessage(PluginInstance.prefix().append(
            Component.text(String.format("Du hast deinen Highscore gebrochen! Dein neuer Highscore ist %s!", currentScore))));
        player.playSound(Sound.sound(Key.key("item.totem.use"), Source.MASTER, 100f, 1f), Emitter.self());

        player.showTitle(Title.title(Component.text("Rekord!").color(PluginColor.BLUE_MID), Component.text("Du hast einen neuen persönlichen Rekord aufgestellt.").color(PluginColor.DARK_GRAY), Times.times(
            Duration.of(1, ChronoUnit.SECONDS), Duration.of(2, ChronoUnit.SECONDS), Duration.of(1, ChronoUnit.SECONDS))));

        /*
        Firework firework = player.getWorld().spawn(player.getLocation(), Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();
        FireworkEffect effect = FireworkEffect.builder()
            .withFade(Color.fromRGB(187, 214, 223), Color.fromRGB(217, 242, 246), Color.fromRGB(180, 220, 231), Color.fromRGB(200, 224, 232))
            .withColor(Color.fromRGB(187, 214, 223), Color.fromRGB(217, 242, 246), Color.fromRGB(180, 220, 231), Color.fromRGB(200, 224, 232))
            .trail(true)
            .build();

        meta.setPower(2);
        meta.clearEffects();
        meta.addEffect(effect);
        firework.setFireworkMeta(meta);
        firework.detonate();
        */
      }
    });
  }


  public void onQuit(Player player) {
    this.saveHighScore(player);
    this.savePoints(player);

    this.currentPoints.remove(player);
    this.awaitingHighScores.remove(player);


    if(this.isJumping(player)){
      this.remove(player);
    }
  }

  public boolean isJumping(Player player) {
    return this.jumpAndRun.getPlayers().contains(player);
  }
}
