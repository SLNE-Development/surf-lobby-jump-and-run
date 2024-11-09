package dev.slne.surf.lobby.jar.papi;

import dev.slne.surf.lobby.jar.mysql.Database;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ParkourPlaceholderExtension extends PlaceholderExpansion {

  @Override
  public @NotNull String getIdentifier() {
    return "surf-lobby-parkour";
  }

  @Override
  public @NotNull String getAuthor() {
    return "SLNE Development, TheBjoRedCraft";
  }

  @Override
  public @NotNull String getVersion() {
    return "1.0.0";
  }

  @Override
  public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
    String[] parts = params.split("_");

    if (parts.length < 3) {
      return null;
    }

    String category = parts[0];
    int place;

    try {
      place = Integer.parseInt(parts[1]);
    } catch (NumberFormatException e) {
      return null;
    }

    if (category.equals("highscore")) {
      if (params.endsWith("name")) {
        return getName(place, getSortedHighScores());
      } else if (params.endsWith("value")) {
        return String.valueOf(getHighScore(place));
      }
    } else if (category.equals("points")) {
      if (params.endsWith("name")) {
        return getName(place, getSortedPoints());
      } else if (params.endsWith("value")) {
        return String.valueOf(getPoints(place));
      }
    }

    return null;
  }

  private String getName(int place, ObjectList<UUID> sortedPlayers) {
    if (place <= 0 || place > sortedPlayers.size()) {
      return "/";
    }

    UUID uuid = sortedPlayers.get(place - 1);
    return getName(uuid);
  }

  private int getHighScore(int place) {
    ObjectList<UUID> sortedPlayers = getSortedHighScores();

    if (place <= 0 || place > sortedPlayers.size()) {
      return -1;
    }

    UUID uuid = sortedPlayers.get(place - 1);
    return Database.getHighScore(uuid);
  }

  private int getPoints(int place) {
    ObjectList<UUID> sortedPlayers = getSortedPoints();

    if (place <= 0 || place > sortedPlayers.size()) {
      return -1;
    }

    UUID uuid = sortedPlayers.get(place - 1);
    return Database.getPoints(uuid);
  }

  private String getName(UUID uuid) {
    OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
    return player.getName() != null ? player.getName() : "Unknown";
  }

  private ObjectList<UUID> getSortedHighScores() {
    Object2ObjectMap<UUID, Integer> highScores = Database.getHighScores();

    return highScores
        .entrySet()
        .stream()
        .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
        .map(Entry::getKey)
        .collect(Collectors.toCollection(ObjectArrayList::new));
  }

  private ObjectList<UUID> getSortedPoints() {
    Object2ObjectMap<UUID, Integer> points = Database.getPoints();

    return points
        .entrySet()
        .stream()
        .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
        .map(Entry::getKey)
        .collect(Collectors.toCollection(ObjectArrayList::new));
  }
}
