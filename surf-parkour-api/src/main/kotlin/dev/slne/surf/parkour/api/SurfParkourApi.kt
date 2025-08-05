package dev.slne.surf.parkour.api

import dev.slne.surf.parkour.api.entity.ParkourPlayer
import dev.slne.surf.parkour.api.model.parkour.Parkour
import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.*

interface SurfParkourApi {
    fun getPlayer(name: String): ParkourPlayer?
    fun getPlayer(uuid: UUID): ParkourPlayer?

    fun getParkour(name: String): Parkour?
    fun getParkour(uuid: UUID): Parkour?
    fun getParkours(): ObjectSet<Parkour>

    companion object {
        val INSTANCE = requiredService<SurfParkourApi>()
    }
}

val surfParkourApi get() = SurfParkourApi.INSTANCE