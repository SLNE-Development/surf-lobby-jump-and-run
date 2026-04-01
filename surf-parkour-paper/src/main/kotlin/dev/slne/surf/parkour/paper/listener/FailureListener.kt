package dev.slne.surf.parkour.paper.listener

import dev.slne.surf.parkour.paper.service.ParkourService
import dev.slne.surf.parkour.paper.util.allOfType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.util.Vector

object FailureListener : Listener {
    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        if (!event.hasChangedBlock()) {
            return
        }

        val player = event.player
        val parkour = ParkourService.getParkour(player) ?: return
        val generator = parkour.getGenerator(player) ?: return

        if (!generator.isRunning()) {
            return
        }

        generator.blockLocations.allOfType<Vector> {
            it.y > event.to.y
        }.also {
            if (it) {
                ParkourService.triggerFailure(player)
            }
        }
    }

    @EventHandler
    fun onDisconnect(event: PlayerQuitEvent) {
        val player = event.player

        val parkour = ParkourService.getParkour(player) ?: return
        val generator = parkour.getGenerator(player) ?: return

        if (generator.isRunning()) {
            ParkourService.triggerFailure(player)
        }
    }
}