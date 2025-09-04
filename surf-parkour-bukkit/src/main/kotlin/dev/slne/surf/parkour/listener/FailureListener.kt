package dev.slne.surf.parkour.listener

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.parkour.core.service.parkourService
import dev.slne.surf.parkour.plugin
import dev.slne.surf.parkour.util.parkourPlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

class FailureListener : Listener {
    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        if (!event.hasChangedBlock()) {
            return
        }

        plugin.launch(plugin.entityDispatcher(event.player)) {
            val player = event.parkourPlayer()
            val parkour = parkourService.getParkour(player) ?: run {
                return@launch
            }
            val generator = parkour.generators[player.uuid] ?: run {
                return@launch
            }

            generator.currentBlock()?.let { block ->
                val playerY = event.to.y
                val blockY = block.y.toDouble()

                if (playerY < blockY) {
                    parkour.onFailure(player)
                }
            }
        }
    }
}