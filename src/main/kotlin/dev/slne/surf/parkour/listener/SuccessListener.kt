package dev.slne.surf.parkour.listener

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.parkour.plugin
import dev.slne.surf.parkour.service.parkourService
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.util.Vector

class SuccessListener : Listener {
    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        if (!event.hasChangedBlock()) {
            return
        }

        val player = event.player

        plugin.launch(plugin.entityDispatcher(event.player)) {
            val parkour = parkourService.getParkour(player) ?: return@launch
            val generator = parkour.getGenerator(player) ?: return@launch

            if (generator.blockLocations.second.equalsBlock(event.to.block.getRelative(BlockFace.DOWN).location.toVector())) {
                parkourService.triggerSuccess(player)
            }
        }
    }

    private fun Vector.equalsBlock(other: Vector): Boolean {
        return this.blockX == other.blockX &&
                this.blockY == other.blockY &&
                this.blockZ == other.blockZ
    }
}