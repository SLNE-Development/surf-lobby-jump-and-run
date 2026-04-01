package dev.slne.surf.parkour.paper.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.parkour.core.paper.service.ParkourRunsService
import dev.slne.surf.parkour.paper.plugin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

object JoinListener : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        plugin.launch {
            ParkourRunsService.loadAndCacheStats(event.player.uniqueId)
        }
    }
}