package dev.slne.surf.parkour.listener

import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mccoroutine.folia.regionDispatcher
import dev.slne.surf.parkour.database.DatabaseProvider
import dev.slne.surf.parkour.plugin
import kotlinx.coroutines.withContext
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.util.Vector

class PlayerMoveListener : Listener {
    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        plugin.launch {
            withContext(plugin.regionDispatcher(event.to)) {
                val player = event.player

                if (!event.hasExplicitlyChangedBlock()) {
                    return@withContext
                }

                DatabaseProvider.getParkours().forEach { parkour ->
                    if(parkour.start.equalsLocation(event.to)) {
                        parkour.startParkour(player)
                    }
                }
            }
        }
    }

    private fun Vector.equalsLocation(location: Location): Boolean = this.blockX == location.blockX && this.blockY == location.blockX && this.blockZ == location.blockZ
}