package dev.slne.surf.parkour.listener

import dev.slne.surf.parkour.SurfParkour
import dev.slne.surf.parkour.menu.ParkourMenu
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

class PlayerInteractListener(): Listener {
    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        val player = event.player
        val item = event.item ?: return

        if(item == SurfParkour.clickItem) {
            ParkourMenu(player)
        }
    }
}