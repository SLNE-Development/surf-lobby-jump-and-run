package dev.slne.surf.parkour.listener

import dev.slne.surf.parkour.SurfParkour
import dev.slne.surf.parkour.menu.ParkourMenu
import dev.slne.surf.parkour.plugin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

class PlayerInteractListener : Listener {
    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        val player = event.player
        val item = event.item ?: return

        if (item == plugin.getInventoryItem(plugin.betaMode)) {
            ParkourMenu.lazyOpen(player)
            event.isCancelled = true
        }
    }
}