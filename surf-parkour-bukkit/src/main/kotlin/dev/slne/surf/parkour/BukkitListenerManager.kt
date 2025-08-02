package dev.slne.surf.parkour

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListenerPriority
import dev.slne.surf.parkour.listener.ParkourPacketListener

object BukkitListenerManager {
    fun registerBukkitListeners() {

    }

    fun registerPacketListeners() {
        PacketEvents.getAPI().eventManager.registerListener(
            ParkourPacketListener(),
            PacketListenerPriority.NORMAL
        )
    }
}