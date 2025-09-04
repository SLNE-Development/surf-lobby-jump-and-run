package dev.slne.surf.parkour.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.parkour.api.entity.ParkourPlayer
import dev.slne.surf.parkour.api.model.parkour.Parkour
import dev.slne.surf.parkour.api.model.parkour.ParkourCreationData
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
        parkourRegistry.getParkours().find { it.players.map { it.uuid }.contains(player.uuid) }

    override fun getParkour(name: String) = parkourRegistry.getParkour(name)
    override fun getParkour(uuid: UUID) = parkourRegistry.getParkour(uuid)
    override fun inParkour(player: ParkourPlayer) =
        parkourRegistry.getParkours().any { it.players.contains(player) }

    override fun getParkours() = parkourRegistry.getParkours()
    override suspend fun onFailure(
        parkour: Parkour,
        player: ParkourPlayer
    ) = parkour.onFailure(player)

    override suspend fun onSuccess(
        parkour: Parkour,
        player: ParkourPlayer,
        index: Int
    ) = parkour.onSuccess(player, index)
}