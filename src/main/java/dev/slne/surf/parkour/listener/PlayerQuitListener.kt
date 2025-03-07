package dev.slne.surf.parkour.listener

import dev.slne.surf.parkour.database.DatabaseProvider
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class PlayerQuitListener(): Listener {
    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        DatabaseProvider.invalidate(event.player.uniqueId)
    }
}