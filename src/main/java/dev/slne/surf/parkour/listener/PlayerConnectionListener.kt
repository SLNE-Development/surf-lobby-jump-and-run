package dev.slne.surf.parkour.listener

import dev.slne.surf.parkour.SurfParkour
import dev.slne.surf.parkour.database.DatabaseProvider

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerKickEvent
import org.bukkit.event.player.PlayerQuitEvent

class PlayerConnectionListener(): Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        event.player.inventory.setItem(2, SurfParkour.clickItem)
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        DatabaseProvider.invalidate(event.player.uniqueId)

        event.player.inventory.removeItem(SurfParkour.clickItem)
    }

    @EventHandler
    fun onKick(event: PlayerKickEvent) {
        if (event.cause == PlayerKickEvent.Cause.FLYING_PLAYER) {
            event.isCancelled = true
        }
    }
}