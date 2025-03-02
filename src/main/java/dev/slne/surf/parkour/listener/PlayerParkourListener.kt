package dev.slne.surf.parkour.listener

import dev.slne.surf.parkour.service.JumpAndRunService
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent

class PlayerParkourListener : Listener {
    @EventHandler
    suspend fun onMove(event: PlayerMoveEvent) {
        if (!event.hasChangedPosition()) {
            retur
        }

        val toBlock = event.to.block
        val block = toBlock.getRelative(BlockFace.DOWN)
        val player = event.player
        val jumps = JumpAndRunService.getLatestJumps(player)
        val world = JumpAndRunService.jumpAndRun.world ?: return
        val start = JumpAndRunService.jumpAndRun.start ?: return
        val startLocation = start.toLocation(world)

        if (toBlock.location == startLocation.block.location) {
            JumpAndRunService.start(player)
            return
        }

        if (jumps.isEmpty() || jumps.size < 2) {
            return
        }

        if (jumps[0] == null || jumps[1] == null) {
            return
        }

        val jump1: Block = jumps[0] ?: return
        val jump2: Block = jumps[1] ?: return

        val playerLocation = player.location
        if (playerLocation.y < jump1.location.y && playerLocation.y < jump2.location.y) {
            JumpAndRunService.remove(player)
            return
        }

        if (block == jumps[1]) {
            val material = JumpAndRunService.blocks[player] ?: return

            jump2.type = material

            JumpAndRunService.addPoint(player)
            JumpAndRunService.checkHighScore(player)
            JumpAndRunService.generate(player)
        }
    }

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        val player = event.player
        val location = event.interactionPoint ?: return

        val jumps: Array<Block?> = JumpAndRunService.getLatestJumps(player)

        for (jump in jumps) {
            if (jump == null) {
                continue
            }

            if (jump.location == location) {
                player.sendBlockChange(jump.location, jump.blockData)
            }
        }
    }

    @EventHandler
    suspend fun onQuit(event: PlayerQuitEvent) {
        val player = event.player

        JumpAndRunService.onQuit(player)
    }
}
