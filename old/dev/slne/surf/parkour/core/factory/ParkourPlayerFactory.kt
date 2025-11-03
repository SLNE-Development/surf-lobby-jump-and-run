package dev.slne.surf.parkour.core.factory

import dev.slne.surf.parkour.api.entity.ParkourPlayer
import dev.slne.surf.surfapi.core.api.util.requiredService
import java.util.*

interface ParkourPlayerFactory {
    suspend fun createPlayer(uuid: UUID): ParkourPlayer
    suspend fun createPlayer(name: String): ParkourPlayer

    companion object {
        val INSTANCE = requiredService<ParkourPlayerFactory>()
    }
}

val parkourPlayerFactory get() = ParkourPlayerFactory.INSTANCE