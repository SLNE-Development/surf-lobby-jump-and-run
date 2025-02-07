package dev.slne.surf.lobby.jar.util

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import dev.slne.surf.lobby.jar.plugin
import org.bukkit.event.Event
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import kotlin.coroutines.CoroutineContext

object DispatcherUtil {
    val PARKOUR = mapOf<Class<out Event>, (event: Event) -> CoroutineContext>(
        PlayerInteractEvent::class.java to { event -> plugin.entityDispatcher((event as PlayerInteractEvent).player) },
        PlayerMoveEvent::class.java to { event -> plugin.entityDispatcher((event as PlayerMoveEvent).player) },
        PlayerQuitEvent::class.java to { event -> plugin.entityDispatcher((event as PlayerQuitEvent).player) }
    )
}