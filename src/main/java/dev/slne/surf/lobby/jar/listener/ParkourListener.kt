package dev.slne.surf.lobby.jar.listener

import dev.slne.surf.lobby.jar.JumpAndRunService
import dev.slne.surf.lobby.jar.PluginInstance
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent

class ParkourListener : Listener {
    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        if (!event.hasChangedPosition()) {
            return
        }

        val toBlock = event.to.block
        val block = toBlock.getRelative(BlockFace.DOWN)
        val player = event.player
        val jumps = JumpAndRunService.getLatestJumps(player)

        if (toBlock.location == JumpAndRunService.jumpAndRun.start!!.block.location) {
            JumpAndRunService.start(player)
            return
        }

        if (jumps.isEmpty() || jumps.size < 2) {
            return
        }



        val playerLocation = player.location
        if (playerLocation.y < jumps[0].location.y && playerLocation.y < jumps[1].location.y) {
            JumpAndRunService.remove(player)
            return
        }

        if (block == jumps[1]) {
            val material = JumpAndRunService.blocks[player] ?: return

            jumps[1].type = material

            JumpAndRunService.addPoint(player)
            JumpAndRunService.checkHighScore(player)
            JumpAndRunService.generate(player)
        }
    }

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        val player = event.player
        val location = event.interactionPoint ?: return

        val jumps: Array<Block> = JumpAndRunService.getLatestJumps(player)

        for (jump in jumps) {
            if (jump.location == location) {
                player.sendBlockChange(jump.location, jump.blockData)
            }
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player

        JumpAndRunService.onQuit(player)
    }
}
