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
  private final String displayName;

  private Location posOne;
  private Location posTwo;
  private Location spawn;
  private Location start;

  private final ObjectList<Player> players;
  private final ObjectList<Material> materials;
  private final Object2ObjectMap<Player, Block> latestBlocks;

  public void kick(Player player){
    PluginInstance.instance().jumpAndRunProvider().remove(player);
  }

  public void join(Player player){
    PluginInstance.instance().jumpAndRunProvider().start(player);
  }
}
