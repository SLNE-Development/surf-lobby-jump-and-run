package dev.slne.surf.parkour.paper.listener

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.parkour.paper.plugin
import dev.slne.surf.parkour.paper.service.parkourService
import dev.slne.surf.parkour.paper.util.getBlocksBelowLocation
import dev.slne.surf.parkour.paper.util.isSameBlock
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

class SuccessListener : Listener {
    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        val player = event.player


        if (!event.hasChangedBlock()) {
            return
        }

        plugin.launch(plugin.entityDispatcher(event.player)) {
            val parkour = parkourService.getParkour(player) ?: return@launch
            val generator = parkour.getGenerator(player) ?: return@launch

            if (!generator.isRunning()) {
                return@launch
            }

            // Check if any block below the destination location matches the target jump block
            // This accounts for landing on block edges
            val blocksBelow = getBlocksBelowLocation(event.to)
            val targetBlock = generator.blockLocations.second

            if (blocksBelow.any { it.isSameBlock(targetBlock) }) {
                parkourService.triggerSuccess(player)
            }
        }
    }
}