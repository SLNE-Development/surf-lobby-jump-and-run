package dev.slne.surf.parkour.listener

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.parkour.SurfParkour
import dev.slne.surf.parkour.database.DatabaseProvider
import dev.slne.surf.parkour.plugin

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerKickEvent
import org.bukkit.event.player.PlayerQuitEvent

class PlayerConnectionListener: Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        plugin.launch(plugin.entityDispatcher(player)) {
            player.inventory.setItem(2, SurfParkour.clickItem)
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        DatabaseProvider.invalidate(event.player.uniqueId)

        event.player.inventory.removeItem(SurfParkour.clickItem) // TODO: 08.03.2025 10:29 - not supported / may not always work
    }

    @EventHandler
    fun onKick(event: PlayerKickEvent) {
        if (event.cause == PlayerKickEvent.Cause.FLYING_PLAYER) {
            event.isCancelled = true
        }
    }
}