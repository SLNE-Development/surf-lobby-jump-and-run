package dev.slne.surf.parkour

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListenerPriority
import dev.slne.surf.parkour.listener.*
import dev.slne.surf.surfapi.bukkit.api.event.register

object BukkitListenerManager {
    fun registerBukkitListeners() {
        FailureListener().register()
        SuccessListener().register()
    }

    fun registerExternalListeners() {
        PacketEvents.getAPI().eventManager.registerListener(
            ParkourPacketListener(),
            PacketListenerPriority.NORMAL
        )

        PacketEvents.getAPI().eventManager.registerListener(
            PlayerPacketListener(),
            PacketListenerPriority.NORMAL
        )

        VulcanEventListener.register()
        PolarMitigationListener().register()
    }
}