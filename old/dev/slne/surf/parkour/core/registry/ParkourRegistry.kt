package dev.slne.surf.parkour.core.registry

import dev.slne.surf.parkour.api.entity.ParkourPlayer
import dev.slne.surf.parkour.api.model.parkour.Parkour
import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.*

interface ParkourRegistry {
    fun registerParkour(parkour: Parkour): Parkour?
    fun unregisterParkour(parkour: Parkour): Parkour?

    fun getParkour(name: String): Parkour?
    fun getParkour(uuid: UUID): Parkour?
    fun getParkours(): ObjectSet<Parkour>

    fun addPlayer(player: ParkourPlayer, parkour: Parkour)
    fun removePlayer(player: ParkourPlayer, parkour: Parkour)

    companion object {
        val INSTANCE = requiredService<ParkourRegistry>()
    }
}

val parkourRegistry get() = ParkourRegistry.INSTANCE