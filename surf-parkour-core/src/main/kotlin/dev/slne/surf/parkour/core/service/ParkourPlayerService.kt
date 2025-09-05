package dev.slne.surf.parkour.core.service

import dev.slne.surf.parkour.api.entity.ParkourPlayer
import dev.slne.surf.surfapi.core.api.util.requiredService
import java.util.*

interface ParkourPlayerService {
    suspend fun loadPlayer(uuid: UUID): ParkourPlayer
    suspend fun loadPlayer(name: String): ParkourPlayer

    suspend fun insertPlayer(player: ParkourPlayer)

    suspend fun getPlayer(uuid: UUID): ParkourPlayer
    suspend fun getPlayer(name: String): ParkourPlayer

    companion object {
        val INSTANCE = requiredService<ParkourPlayerService>()
    }
}

val parkourPlayerService get() = ParkourPlayerService.INSTANCE