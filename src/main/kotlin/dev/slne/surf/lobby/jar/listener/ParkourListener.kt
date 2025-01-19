package dev.slne.surf.lobby.jar.listener

import dev.slne.surf.lobby.jar.service.JnrService
import dev.slne.surf.lobby.jar.util.toJnrPlayer
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.springframework.stereotype.Component

@Component
class ParkourListener(private val jnrService: JnrService) : Listener {

    @EventHandler
    suspend fun onMove(event: PlayerMoveEvent) {
        if (!event.hasChangedPosition()) {
            return
        }

        val toBlock = event.to.block
        val block = toBlock.getRelative(BlockFace.DOWN)
        val player = event.player
        val jnrPlayer = player.toJnrPlayer()

        val generator = jnrService.currentJumpAndRuns[jnrPlayer]
        val jumps = generator?.latestJumps ?: return

        val startLocation = jnrService.jumpAndRun.start ?: return

        if (toBlock.location.toVector() == startLocation) {
            jnrService.start(jnrPlayer)
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
            jnrService.remove(jnrPlayer)
            return
        }

        if (block == jumps[1]) {
            val material = jnrService.blocks[player] ?: return

            jump2.type = material

            jnrPlayer.incrementPoints()
            generator.generate()
        }
    }

    @EventHandler
    suspend fun onInteract(event: PlayerInteractEvent) {
        val player = event.player
        val location = event.interactionPoint ?: return

        val generator = jnrService.currentJumpAndRuns[player.toJnrPlayer()]
        val jumps: Array<Block?> = generator?.latestJumps ?: return

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
        jnrService.onQuit(event.player.toJnrPlayer())
    }
}
