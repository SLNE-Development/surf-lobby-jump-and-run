package dev.slne.surf.parkour.listener

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.parkour.plugin
import dev.slne.surf.parkour.service.parkourService
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

class FailureListener : Listener {
    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        if (!event.hasChangedBlock()) {
            return
        }

        val player = event.player

        plugin.launch(plugin.entityDispatcher(player)) {
            val parkour = parkourService.getParkour(player) ?: run {
                return@launch
            }

            val generator = parkour.getGenerator(player) ?: run {
                return@launch
            }

            val current = generator.blockLocations.first
            val next = generator.blockLocations.third

            val playerY = event.to.blockY

            if (playerY < current.blockX && playerY < next.blockY) {
                parkourService.triggerFailure(player)
            }
        }
    }
}