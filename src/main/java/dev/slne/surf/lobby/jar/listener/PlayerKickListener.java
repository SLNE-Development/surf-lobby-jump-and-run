package dev.slne.surf.lobby.jar.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerKickEvent.Cause;

public class PlayerKickListener implements Listener {
  @EventHandler
  public void onKick(PlayerKickEvent event) {
    if(event.getCause().equals(Cause.FLYING_PLAYER)) {
      event.setCancelled(true);
    }
  }
}
