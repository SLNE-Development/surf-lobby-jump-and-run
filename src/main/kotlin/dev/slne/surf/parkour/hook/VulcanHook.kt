package dev.slne.surf.parkour.hook

import dev.slne.surf.parkour.plugin
import dev.slne.surf.parkour.service.parkourService
import dev.slne.surf.parkour.util.anyOfType
import dev.slne.surf.parkour.util.getBlocksBelowFeet
import dev.slne.surf.parkour.util.isSameBlock
import dev.slne.surf.surfapi.bukkit.api.event.register
import me.frep.vulcan.api.event.VulcanGhostBlockEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.util.Vector

class VulcanHook : Listener {
    @EventHandler
    fun onPlayerGhostBlock(event: VulcanGhostBlockEvent) {
        if (this.isStandingOnJumpBlock(event.player)) {
            event.isCancelled = true
        }
    }

    fun register() {
        if (!Bukkit.getPluginManager().isPluginEnabled("Vulcan")) {
            return
        }

        this.register(plugin)
    }

    private fun isStandingOnJumpBlock(player: Player): Boolean {
        val playerParkour = parkourService.getParkour(player) ?: return false
        val generator = playerParkour.generators[player.uniqueId] ?: return false

        // Get the blocks below the player's feet (checking corners to handle edge cases)
        val blocksBelow = player.getBlocksBelowFeet()
        
        return generator.blockLocations
            .anyOfType<Vector> { jumpBlock ->
                blocksBelow.any { blockBelow ->
                    jumpBlock.isSameBlock(blockBelow)
                }
            }
    }
}