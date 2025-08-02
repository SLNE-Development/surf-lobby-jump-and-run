package dev.slne.surf.parkour

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListenerPriority
import dev.slne.surf.parkour.listener.ParkourPacketListener
import dev.slne.surf.parkour.listener.PolarMitigationListener

object BukkitListenerManager {
    fun registerBukkitListeners() {

    }

    fun registerExternalListeners() {
        PacketEvents.getAPI().eventManager.registerListener(
            ParkourPacketListener(),
            PacketListenerPriority.NORMAL
        )

        PolarMitigationListener().register()
    }
}