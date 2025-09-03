package dev.slne.surf.parkour.listener

import dev.slne.surf.parkour.core.service.parkourService
import dev.slne.surf.parkour.plugin
import dev.slne.surf.parkour.util.appendLocalPrefix
import dev.slne.surf.parkour.util.parkourPlayer
import dev.slne.surf.surfapi.bukkit.api.event.register
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import me.frep.vulcan.api.event.VulcanGhostBlockEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object VulcanEventListener : Listener {
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

        plugin.componentLogger.info(buildText {
            appendLocalPrefix()
            success("Hooked into the Vulcanic Eruption!")
        })
    }

    private fun isStandingOnJumpBlock(player: Player): Boolean {
        val playerParkour = parkourService.getParkour(player.parkourPlayer()) ?: return false
        val generator = playerParkour.generator[player.uniqueId] ?: return false

        return generator.getRegisteredBlocks()
            .any { it.equals(player.location.clone().add(0.0, -1.0, 0.0).block) }
    }
}