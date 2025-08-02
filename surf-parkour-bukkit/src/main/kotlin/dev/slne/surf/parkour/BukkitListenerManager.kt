package dev.slne.surf.parkour

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListenerPriority

import dev.slne.surf.parkour.listener.ParkourPacketListener
import dev.slne.surf.parkour.listener.PolarMitigationListener
import dev.slne.surf.parkour.listener.VulcanEventListener
import dev.slne.surf.surfapi.bukkit.api.event.register

object BukkitListenerManager {
    fun registerBukkitListeners() {
        VulcanEventListener().register()
    }

    fun registerExternalListeners() {
        PacketEvents.getAPI().eventManager.registerListener(
            ParkourPacketListener(),
            PacketListenerPriority.NORMAL
        )

        PolarMitigationListener().register()
    }
}