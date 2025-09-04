package dev.slne.surf.parkour.fallback.registry

import com.google.auto.service.AutoService
import dev.slne.surf.parkour.api.entity.ParkourPlayer
import dev.slne.surf.parkour.api.model.parkour.Parkour
import dev.slne.surf.parkour.core.registry.ParkourRegistry
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import net.kyori.adventure.util.Services
import java.util.*

@AutoService(ParkourRegistry::class)
class FallbackParkourRegistry : ParkourRegistry, Services.Fallback {
    val parkours = mutableObject2ObjectMapOf<UUID, Parkour>()

    override fun registerParkour(parkour: Parkour) = parkours.put(parkour.uuid, parkour)
    override fun unregisterParkour(parkour: Parkour) = parkours.remove(parkour.uuid)
    override fun getParkour(name: String) = parkours.values
        .firstOrNull { it.name.equals(name, ignoreCase = true) }

    override fun getParkour(uuid: UUID) = parkours[uuid]
    override fun getParkours() = parkours.values.toObjectSet()
    override fun addPlayer(
        player: ParkourPlayer,
        parkour: Parkour
    ) = parkour.modifySaving {
        players.add(player)
    }

    override fun removePlayer(
        player: ParkourPlayer,
        parkour: Parkour
    ) = parkour.modifySaving {
        players.remove(player)
    }
}