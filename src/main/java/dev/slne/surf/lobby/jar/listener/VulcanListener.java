package dev.slne.surf.lobby.jar.listener;

import dev.slne.surf.lobby.jar.PluginInstance;
import me.frep.vulcan.api.event.VulcanGhostBlockEvent;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class VulcanListener implements Listener {
  @EventHandler
  public void onPlayerGhostBlock(VulcanGhostBlockEvent event) {
    if(this.isStandingOnJumpBlock(event.getPlayer())) {
      event.setCancelled(true);
    }
  }

  private boolean isStandingOnJumpBlock(Player player) {
    for (Block latestJump : PluginInstance.instance().jumpAndRunProvider().getLatestJumps(player)) {
      if(latestJump.equals(player.getLocation().clone().add(0, -1, 0).getBlock())) {
        return true;
      }
    }

    return false;
  }
}
