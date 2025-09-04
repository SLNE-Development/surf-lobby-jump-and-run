package dev.slne.surf.parkour.core.service

import dev.slne.surf.parkour.api.model.parkour.Parkour
import dev.slne.surf.parkour.api.model.parkour.statistic.ParkourStatistic
import dev.slne.surf.parkour.core.util.ServiceWithDatabase
import dev.slne.surf.surfapi.core.api.util.requiredService
import java.util.*

interface ParkourStatisticsService : ServiceWithDatabase {
    suspend fun getStatistic(userUuid: UUID, parkour: Parkour): ParkourStatistic
    suspend fun addStatistic(statistic: ParkourStatistic)

    companion object {
        val INSTANCE = requiredService<ParkourStatisticsService>()
    }
}

val parkourStatisticsService get() = ParkourStatisticsService.INSTANCE