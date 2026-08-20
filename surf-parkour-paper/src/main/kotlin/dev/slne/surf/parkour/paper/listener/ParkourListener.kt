package dev.slne.surf.parkour.paper.listener

import dev.slne.surf.parkour.core.client.service.ParkourConnectionService
import dev.slne.surf.parkour.core.client.service.ParkourMoveService
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent

object ParkourListener : Listener {
    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        if (!event.hasChangedBlock()) {
            return
        }

        val to = event.to
        ParkourMoveService.processMove(event.player.uniqueId, to.x, to.y, to.z)
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        ParkourConnectionService.onJoin(event.player.uniqueId)
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        ParkourConnectionService.onQuit(event.player.uniqueId)
    }
}
