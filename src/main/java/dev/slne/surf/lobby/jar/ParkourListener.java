package dev.slne.surf.lobby.jar;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class ParkourListener implements Listener {
  private final JumpAndRunProvider jumpAndRunProvider = PluginInstance.instance().jumpAndRunProvider();

  @EventHandler
  public void onMove(PlayerMoveEvent event) {
    Block block = event.getTo().clone().subtract(0, 1, 0).getBlock();
    Player player = event.getPlayer();
    Block[] jumps = this.jumpAndRunProvider.getLatestJumps(player);

    if(jumps == null){
      return;
    }

    if(player.getLocation().getY() < jumps[0].getLocation().getY() && player.getLocation().getY() < jumps[1].getLocation().getY()){
      jumpAndRunProvider.remove(player);
      return;
    }

    if (jumps[1] == null) {
      return;
    }

    if (block.equals(jumps[1])) {

      jumps[1].setType(jumpAndRunProvider.blocks().get(player));

      this.jumpAndRunProvider.generate(player);
    }
  }
}
