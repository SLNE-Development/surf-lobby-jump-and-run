package dev.slne.surf.parkour.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerKickEvent

class PlayerKickListener : Listener {
    @EventHandler
    fun onKick(event: PlayerKickEvent) {
        if (event.cause == PlayerKickEvent.Cause.FLYING_PLAYER) {
            event.isCancelled = true
        }
    }
}
