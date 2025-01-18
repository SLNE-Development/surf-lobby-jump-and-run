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
    private val jumpAndRunProvider: JumpAndRunService? =
        PluginInstance.Companion.instance().jumpAndRunProvider()

    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        if (!event.hasChangedPosition()) {
            return
        }

        val toBlock = event.to.block
        val block = toBlock.getRelative(BlockFace.DOWN)
        val player = event.player
        val jumps = jumpAndRunProvider!!.getLatestJumps(player)

        if (toBlock.location
            == jumpAndRunProvider.jumpAndRun().start.block.location
        ) {
            jumpAndRunProvider.start(player)
            return
        }

        if (jumps == null) {
            return
        }

        if (jumps.size < 2 || jumps[0] == null || jumps[1] == null) {
            return
        }

        val playerLocation = player.location
        if (playerLocation.y < jumps[0]!!.location.y && playerLocation.y < jumps[1]!!
                .location.y
        ) {
            jumpAndRunProvider.remove(player)
            return
        }

        if (jumps[1] == null) {
            return
        }

        if (block == jumps[1]) {
            jumps[1]!!.setType(jumpAndRunProvider.blocks()[player])

            jumpAndRunProvider.addPoint(player)
            jumpAndRunProvider.checkHighScore(player)
            jumpAndRunProvider.generate(player)
        }
    }

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        val player = event.player
        val location = event.interactionPoint ?: return

        val jumps: Array<Block> =
            PluginInstance.Companion.instance().jumpAndRunProvider().getLatestJumps(player)

        for (jump in jumps) {
            if (jump.location == location) {
                player.sendBlockChange(jump.location, jump.blockData)
            }
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player

        jumpAndRunProvider!!.onQuit(player)
    }
}
