package dev.slne.surf.lobby.jar.listener

import dev.slne.surf.lobby.jar.service.JumpAndRunService
import dev.slne.surf.lobby.jar.util.toJnrPlayer
import me.frep.vulcan.api.event.VulcanGhostBlockEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object VulcanListener : Listener {

    @EventHandler
    suspend fun onPlayerGhostBlock(event: VulcanGhostBlockEvent) {
        if (this.isStandingOnJumpBlock(event.player)) {
            event.isCancelled = true
        }
    }

    private suspend fun isStandingOnJumpBlock(player: Player): Boolean {
        val jnrPlayer = player.toJnrPlayer()
        val generator = JumpAndRunService.currentJumpAndRuns[jnrPlayer] ?: return false

        for (latestJump in generator.latestJumps) {
            if (latestJump == player.location.clone().add(0.0, -1.0, 0.0).block) {
                return true
            }
        }

        return false
    }
}
