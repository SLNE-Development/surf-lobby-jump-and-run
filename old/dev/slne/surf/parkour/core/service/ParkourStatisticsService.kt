package dev.slne.surf.parkour.core.service

import dev.slne.surf.parkour.api.model.parkour.statistic.ParkourStatistic
import dev.slne.surf.parkour.api.model.parkour.statistic.ParkourStatisticSummary
import dev.slne.surf.parkour.core.util.TableHolder
import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.*

interface ParkourStatisticsService : TableHolder {
    suspend fun getSummary(userUuid: UUID): ParkourStatisticSummary
    suspend fun getEverySummary(): ObjectSet<ParkourStatisticSummary>

    suspend fun addStatistic(statistic: ParkourStatistic)

    companion object {
        val INSTANCE = requiredService<ParkourStatisticsService>()
    }
}

val parkourStatisticsService get() = ParkourStatisticsService.INSTANCE