package dev.slne.surf.parkour.paper.listener

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.parkour.paper.plugin
import dev.slne.surf.parkour.paper.service.parkourService
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
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

            val possibleBlocks = getBlocksAround(event.to.block.getRelative(BlockFace.DOWN))
            val targetBlock = generator.blockLocations.second

            if (possibleBlocks.any { it.isSameBlock(targetBlock) }) {
                parkourService.triggerSuccess(player)
            }
        }
    }

    private fun Block.isSameBlock(other: Vector): Boolean {
        return this.x == other.blockX && this.y == other.blockY && this.z == other.blockZ
    }

    private fun getBlocksAround(block: Block) = listOf(
        block,
        block.getRelative(1, 0, 0),
        block.getRelative(-1, 0, 0),
        block.getRelative(0, 0, 1),
        block.getRelative(0, 0, -1),
        block.getRelative(1, 0, 1),
        block.getRelative(1, 0, -1),
        block.getRelative(-1, 0, 1),
        block.getRelative(-1, 0, -1)
    )
}