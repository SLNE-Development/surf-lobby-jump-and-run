package dev.slne.surf.parkour.paper.listener

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.parkour.paper.plugin
import dev.slne.surf.parkour.paper.service.parkourService
import dev.slne.surf.parkour.paper.util.allOfType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.util.Vector

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

            if (!generator.isRunning()) {
                return@launch
            }

            generator.blockLocations.allOfType<Vector> {
                it.y > event.to.y
            }.also {
                if (it) {
                    parkourService.triggerFailure(player)
                }
            }
        }
    }

    @EventHandler
    fun onDisconnect(event: PlayerQuitEvent) {
        val player = event.player

        plugin.launch(plugin.entityDispatcher(player)) {
            val parkour = parkourService.getParkour(player) ?: run {
                return@launch
            }

            val generator = parkour.getGenerator(player) ?: run {
                return@launch
            }

            if (generator.isRunning()) {
                parkourService.triggerFailure(player)
            }
        }
    }
}