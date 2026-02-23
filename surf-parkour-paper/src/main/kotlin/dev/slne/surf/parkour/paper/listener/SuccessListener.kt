package dev.slne.surf.parkour.paper.listener

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.parkour.paper.plugin
import dev.slne.surf.parkour.paper.service.parkourService
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.util.Vector

object SuccessListener : Listener {
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

            val target = generator.blockLocations.second
            val playerPos = event.to.toVector()

            if (isWithinBoundingBox(playerPos, target)) {
                parkourService.triggerSuccess(player)
            }
        }
    }

    private fun isWithinBoundingBox(playerPos: Vector, target: Vector): Boolean {
        val halfBlock = 0.5
        val minX = target.x - halfBlock
        val maxX = target.x + halfBlock
        val minY = target.y
        val maxY = target.y + 1.0
        val minZ = target.z - halfBlock
        val maxZ = target.z + halfBlock

        return playerPos.x in minX..maxX &&
                playerPos.y in minY..maxY &&
                playerPos.z in minZ..maxZ
    }
}