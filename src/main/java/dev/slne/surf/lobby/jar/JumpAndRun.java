package dev.slne.surf.lobby.jar;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectList;
import lombok.Builder;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

@Builder
@Getter
public class JumpAndRun {
  private final String id;
  private final String displayName;

  private final Location posOne;
  private final Location posTwo;

  private final Integer difficulty;
  private final ObjectList<Player> players;
  private final ObjectList<Material> materials;
  private final Object2ObjectMap<Player, Block> latestBlocks;

  public void kick(Player player){

  }

  public void join(Player player){

  }
}
