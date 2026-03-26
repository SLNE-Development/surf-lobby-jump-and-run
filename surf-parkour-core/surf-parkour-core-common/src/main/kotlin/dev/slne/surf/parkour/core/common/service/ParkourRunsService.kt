package dev.slne.surf.parkour.core.common.service

import dev.slne.surf.parkour.api.data.ParkourRun
import dev.slne.surf.parkour.api.data.ParkourStats
import dev.slne.surf.surfapi.core.api.util.requiredService
import java.util.*

val parkourRunsService = requiredService<ParkourRunsService>()

interface ParkourRunsService {
    val stats: List<ParkourStats>

    suspend fun loadAndCacheStats(playerUuid: UUID)
    suspend fun saveRun(run: ParkourRun)

    fun getStats(playerUuid: UUID): ParkourStats

    suspend fun loadStats(): Long
}