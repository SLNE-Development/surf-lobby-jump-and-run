package dev.slne.surf.parkour.listener

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.parkour.plugin
import dev.slne.surf.parkour.util.inventoryItem
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class ParkourItemListener : Listener {
    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        val item = event.currentItem ?: return

        if (item == inventoryItem) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onDrop(event: PlayerDropItemEvent) {
        if (event.itemDrop.itemStack == inventoryItem) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        plugin.launch(plugin.entityDispatcher(player)) {
            player.inventory.setItem(6, inventoryItem)
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        event.player.inventory.removeItem(inventoryItem)
    }

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        val player = event.player
        val item = event.item ?: return
    }
}