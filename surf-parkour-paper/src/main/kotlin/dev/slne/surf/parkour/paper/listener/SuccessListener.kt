package dev.slne.surf.parkour.paper.listener

import dev.slne.surf.parkour.paper.service.ParkourService
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

        val parkour = ParkourService.getParkour(player) ?: return
        val generator = parkour.getGenerator(player) ?: return

        if (!generator.isRunning()) {
            return
        }

        val playerPos = event.to.clone().subtract(0.0, 1.0, 0.0)
        val targetBlock = generator.blockLocations.second

        if (isOnTargetBlock(playerPos.x, playerPos.y, playerPos.z, targetBlock)) {
            ParkourService.triggerSuccess(player)
        }
    }

    private fun isOnTargetBlock(px: Double, py: Double, pz: Double, target: Vector): Boolean {
        val blockCenterX = target.blockX + 0.5
        val blockCenterZ = target.blockZ + 0.5

        return py.toInt() == target.blockY
                && px >= blockCenterX - 1.0 && px <= blockCenterX + 1.0
                && pz >= blockCenterZ - 1.0 && pz <= blockCenterZ + 1.0
    }
}