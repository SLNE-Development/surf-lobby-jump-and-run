package dev.slne.surf.parkour.listener

import dev.slne.surf.parkour.parkour.Parkour
import me.frep.vulcan.api.event.VulcanGhostBlockEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class PlayerAnticheatListener() : Listener {
    @EventHandler
    fun onPlayerGhostBlock(event: VulcanGhostBlockEvent) {
        if (this.isStandingOnJumpBlock(event.player)) {
            event.isCancelled = true
        }
    }

    private fun isStandingOnJumpBlock(player: Player): Boolean {
        val playerParkour = Parkour.getParkour(player) ?: return false

        for (latestJump in playerParkour.latestJumps) {
            if (latestJump.equals(player.location.clone().add(0.0, -1.0, 0.0).block)) {
                return true
            }
        }

        return false
    }
}