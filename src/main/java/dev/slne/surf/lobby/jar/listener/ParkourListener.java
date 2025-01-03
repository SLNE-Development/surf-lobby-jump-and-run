package dev.slne.surf.lobby.jar.listener;

import dev.slne.surf.lobby.jar.JumpAndRunProvider;
import dev.slne.surf.lobby.jar.PluginInstance;
import java.util.Arrays;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ParkourListener implements Listener {

  private final JumpAndRunProvider jumpAndRunProvider = PluginInstance.instance().jumpAndRunProvider();

  @EventHandler
  public void onMove(PlayerMoveEvent event) {
    if (!event.hasChangedPosition()) {
      return;
    }

    Block toBlock = event.getTo().getBlock();
    Block block = toBlock.getRelative(BlockFace.DOWN);
    Player player = event.getPlayer();
    Block[] jumps = this.jumpAndRunProvider.getLatestJumps(player);

    if (toBlock.getLocation()
        .equals(jumpAndRunProvider.jumpAndRun().getStart().getBlock().getLocation())) {
      jumpAndRunProvider.start(player);
      return;
    }

    if (jumps == null) {
      return;
    }

    if (jumps.length < 2 || jumps[0] == null || jumps[1] == null) {
      return;
    }

    Location playerLocation = player.getLocation();
    if (playerLocation.getY() < jumps[0].getLocation().getY() && playerLocation.getY() < jumps[1].getLocation().getY()) {
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
  public void onInteract(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    Block block = event.getClickedBlock();

    if(block == null) {

    }

    Block[] jumps = PluginInstance.instance().jumpAndRunProvider().getLatestJumps(player);

    for (Block jump : jumps) {
      if(jump.getLocation().equals(block.getLocation())) {
        player.sendBlockChange(jump.getLocation(), jump.getBlockData());
      }
    }
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    Player player = event.getPlayer();

    jumpAndRunProvider.onQuit(player);
  }
}
