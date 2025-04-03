package dev.slne.surf.parkour.listener

import dev.slne.surf.parkour.plugin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class PlayerInventoryListener() : Listener {
    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        if (event.currentItem == null) {
            return
        }

        if (event.currentItem == plugin.getInventoryItem(plugin.betaMode)) {
            event.isCancelled = true
        }
    }
}