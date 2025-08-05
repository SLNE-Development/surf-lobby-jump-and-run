package dev.slne.surf.parkour.api.event

import dev.slne.surf.parkour.api.entity.ParkourPlayer
import dev.slne.surf.parkour.api.model.parkour.Parkour
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class ParkourSuccessEvent(
    val parkour: Parkour,
    val player: ParkourPlayer,
    val jumpIndex: Int
) : Event() {
    companion object {
        private val handlers = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return handlers
        }
    }

    override fun getHandlers(): HandlerList {
        return Companion.handlers
    }
}