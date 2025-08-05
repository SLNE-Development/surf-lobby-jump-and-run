package dev.slne.surf.parkour.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.parkour.core.service.parkourService
import dev.slne.surf.parkour.plugin
import dev.slne.surf.parkour.util.equalsBlockLocation
import dev.slne.surf.parkour.util.parkourPlayer
import kotlinx.coroutines.Dispatchers
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

class SuccessListener : Listener {
    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        if (!event.hasChangedBlock()) {
            return
        }

        plugin.launch(Dispatchers.IO) {
            val player = event.parkourPlayer()
            val parkour = parkourService.getParkour(player) ?: return@launch
            val generator = parkour.generator[player.uuid] ?: return@launch

            if (generator.getRegisteredBlocks()
                    .any { it.location.equalsBlockLocation(event.to.subtract(0.0, 1.0, 0.0)) }
            ) {
                parkour.onSuccess(player, generator.currentIndex())
            }
        }
    }
}