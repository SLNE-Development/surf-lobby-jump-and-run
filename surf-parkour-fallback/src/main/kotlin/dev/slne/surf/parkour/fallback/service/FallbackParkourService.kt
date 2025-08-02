package dev.slne.surf.parkour.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.parkour.api.entity.ParkourPlayer
import dev.slne.surf.parkour.api.model.Parkour
import dev.slne.surf.parkour.api.model.ParkourCreationData
import dev.slne.surf.parkour.core.factory.parkourFactory
import dev.slne.surf.parkour.core.registry.parkourRegistry
import dev.slne.surf.parkour.core.service.ParkourService
import net.kyori.adventure.util.Services
import java.util.*

@AutoService(ParkourService::class)
class FallbackParkourService : ParkourService, Services.Fallback {
    override fun createParkour(parkourData: ParkourCreationData): Parkour {
        val parkour = parkourFactory.createParkour(parkourData)
        parkourRegistry.registerParkour(parkour)

        return parkour
    }

    override fun deleteParkour(parkour: Parkour) {
        parkour.players.forEach {
            it.player()?.teleportAsync(parkour.spawnLocation)
        }
    }

    override fun getParkour(player: ParkourPlayer) =
        parkourRegistry.getParkours().find { it.players.contains(player) }

    override fun getParkour(name: String) = parkourRegistry.getParkour(name)
    override fun getParkour(uuid: UUID) = parkourRegistry.getParkour(uuid)
}