package dev.slne.surf.parkour.paper.listener

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.parkour.paper.plugin
import dev.slne.surf.parkour.paper.util.inventoryItem
import dev.slne.surf.surfapi.bukkit.api.event.cancel
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.*

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

        if (item == inventoryItem) {
            plugin.launch {

            }
            event.cancel()
        }
    }

    @EventHandler
    fun onOffhandSwitch(event: PlayerSwapHandItemsEvent) {
        if (event.offHandItem == inventoryItem) {
            event.isCancelled = true
        }
    }
}