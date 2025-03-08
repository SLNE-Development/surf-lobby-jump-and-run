package dev.slne.surf.parkour.listener

import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mccoroutine.folia.regionDispatcher
import dev.slne.surf.parkour.parkour.Parkour
import dev.slne.surf.parkour.plugin
import kotlinx.coroutines.withContext

import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent

class PlayerParkourListener : Listener {
    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        if (!event.hasChangedPosition()) {
            return
        }

        val player = event.player
        val parkour = Parkour.getParkour(player) ?: return
        val jumps = parkour.latestJumps[player.uniqueId] ?: return

        if (jumps.isEmpty() || jumps.size < 2) {
            return
        }

        if (jumps[0] == null || jumps[1] == null) {
            return
        }

        val jump1: Block = jumps[0] ?: return
        val jump2: Block = jumps[1] ?: return


        plugin.launch {
            if (player.location.y < jump1.location.y && player.location.y < jump2.location.y) {
                parkour.announceParkourLoose(player)
                parkour.cancelParkour(player)
                return@launch
            }

            val to = event.to
            withContext(plugin.regionDispatcher(to)) {
                if (to.block.getRelative(BlockFace.DOWN) == jumps[1]) {
                    val material = parkour.blocks[player.uniqueId] ?: return@withContext

                    player.sendBlockChange(jump2.location, material.createBlockData())

                    parkour.increasePoints(player)
                    parkour.announceNewScoredPoint(player)
                    parkour.generate(player)
                }
            }
        }
    }
}
