package dev.slne.surf.lobby.jar;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectList;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

@Builder
@Getter
@Setter
public class JumpAndRun {
  private final String id;
  private final String displayName;

  private Location posOne;
  private Location posTwo;

  private Integer difficulty;
  private final ObjectList<Player> players;
  private final ObjectList<Material> materials;
  private final Object2ObjectMap<Player, Block> latestBlocks;

  public void kick(Player player){

  }

  public void join(Player player){

  }
}
