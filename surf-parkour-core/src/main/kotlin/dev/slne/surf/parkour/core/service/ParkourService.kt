package dev.slne.surf.parkour.core.service

import dev.slne.surf.parkour.api.entity.ParkourPlayer
import dev.slne.surf.parkour.api.model.Parkour
import dev.slne.surf.parkour.api.model.ParkourCreationData
import dev.slne.surf.surfapi.core.api.util.requiredService
import java.util.*

interface ParkourService {
    fun createParkour(parkourData: ParkourCreationData): Parkour
    fun deleteParkour(parkour: Parkour)
    fun getParkour(player: ParkourPlayer): Parkour?
    fun getParkour(name: String): Parkour?
    fun getParkour(uuid: UUID): Parkour?

    companion object {
        val INSTANCE = requiredService<ParkourService>()
    }
}

val parkourService get() = ParkourService.INSTANCE