package dev.slne.surf.parkour.listener

import dev.slne.surf.parkour.plugin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerDropItemEvent

class PlayerDropListener() : Listener {
    @EventHandler
    fun onDrop(event: PlayerDropItemEvent) {
        if(event.itemDrop.itemStack == plugin.getInventoryItem(plugin.betaMode)) {
            event.isCancelled = true
        }
    }
}