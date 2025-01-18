package dev.slne.surf.lobby.jar.listener

import dev.slne.surf.lobby.jar.JumpAndRunService
import dev.slne.surf.lobby.jar.PluginInstance
import me.frep.vulcan.api.event.VulcanGhostBlockEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class VulcanListener : Listener {
    @EventHandler
    fun onPlayerGhostBlock(event: VulcanGhostBlockEvent) {
        if (this.isStandingOnJumpBlock(event.player)) {
            event.isCancelled = true
        }
    }

    private fun isStandingOnJumpBlock(player: Player): Boolean {
        for (latestJump in JumpAndRunService.getLatestJumps(player)) {
            if (latestJump == player.location.clone().add(0.0, -1.0, 0.0).block) {
                return true
            }
        }

        return false
    }
}
