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
                println("Player is not in a parkour")
                println("Parkours: ${parkourService.getParkours().map { it.name }}")
                println(
                    "Parkour players: ${
                        parkourService.getParkours()
                            .map { it.players.joinToString(", ") { it.name } }
                    }"
                )
                return@launch
            }
            val generator = parkour.generator[player.uuid] ?: run {
                println("No generator for player")
                return@launch
            }

            generator.currentBlock()?.let {
                if (it.y < event.to.y) {
                    parkour.onFailure(player)
                }
            }
        }
    }
}