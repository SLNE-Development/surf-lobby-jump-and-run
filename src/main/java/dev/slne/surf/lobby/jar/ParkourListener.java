package dev.slne.surf.lobby.jar;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ParkourListener implements Listener {
  private final JumpAndRunProvider jumpAndRunProvider = PluginInstance.instance().jumpAndRunProvider();

  @EventHandler
  public void onMove(PlayerMoveEvent event) {
    Block block = event.getTo().clone().subtract(0, 1, 0).getBlock();
    Player player = event.getPlayer();
    Block[] jumps = this.jumpAndRunProvider.getLatestJumps(player);

    if(event.getTo().getBlock().getLocation().equals(jumpAndRunProvider.jumpAndRun().getStart().getBlock().getLocation())){
      jumpAndRunProvider.start(player);
      return;
    }

    if(jumps == null){
      return;
    }

    if(jumps[0] == null || jumps[1] == null){
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

      this.jumpAndRunProvider.addPoint(player);
      this.jumpAndRunProvider.checkHighScore(player);
      this.jumpAndRunProvider.generate(player);
    }
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event){
    Player player = event.getPlayer();

    jumpAndRunProvider.onQuit(player);
  }
}
