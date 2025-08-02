package dev.slne.surf.parkour.listener

import dev.slne.surf.parkour.core.service.parkourService
import dev.slne.surf.parkour.util.parkourPlayer
import me.frep.vulcan.api.event.VulcanGhostBlockEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class VulcanEventListener : Listener {
    @EventHandler
    fun onPlayerGhostBlock(event: VulcanGhostBlockEvent) {
        if (this.isStandingOnJumpBlock(event.player)) {
            event.isCancelled = true
        }
    }

    private fun isStandingOnJumpBlock(player: Player): Boolean {
        val playerParkour = parkourService.getParkour(player.parkourPlayer()) ?: return false
        val generator = playerParkour.generator[player.uniqueId] ?: return false

        return generator.getRegisteredBlocks()
            .any { it.equals(player.location.clone().add(0.0, -1.0, 0.0).block) }
    }
}