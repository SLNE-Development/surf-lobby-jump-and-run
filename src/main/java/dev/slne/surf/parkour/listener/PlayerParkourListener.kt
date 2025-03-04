package dev.slne.surf.parkour.listener

import dev.slne.surf.parkour.parkour.Parkour
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent

class PlayerParkourListener : Listener {
    @EventHandler
    suspend fun onMove(event: PlayerMoveEvent) {
        if (!event.hasChangedPosition()) {
            return
        }

        val player = event.player
        val parkour = Parkour.getParkour(player) ?: return
        val jumps = parkour.latestJumps[player] ?: return

        if (jumps.isEmpty() || jumps.size < 2) {
            return
        }

        if (jumps[0] == null || jumps[1] == null) {
            return
        }

        val jump1: Block = jumps[0] ?: return
        val jump2: Block = jumps[1] ?: return


        if (player.location.y < jump1.location.y && player.location.y < jump2.location.y) {
            parkour.announceParkourLoose(player)
            parkour.cancelParkour(player)
            return
        }

        if (event.to.block.getRelative(BlockFace.DOWN) == jumps[1]) {
            val material = parkour.blocks[player] ?: return

            player.sendBlockChange(jump2.location, material.createBlockData())

            parkour.increasePoints(player)
            parkour.announceNewScoredPoint(player)
            parkour.generate(player)
        }
    }

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        val player = event.player
        val location = event.interactionPoint ?: return
        val parkour = Parkour.getParkour(player) ?: return
        val jumps: Array<Block?> = parkour.latestJumps[player] ?: return

        for (jump in jumps) {
            if (jump == null) {
                continue
            }

            if (jump.location == location) {
                player.sendBlockChange(jump.location, jump.blockData)
            }
        }
    }
}
