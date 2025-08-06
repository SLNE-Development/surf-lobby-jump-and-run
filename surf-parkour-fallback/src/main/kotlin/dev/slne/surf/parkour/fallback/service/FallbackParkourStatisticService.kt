package dev.slne.surf.parkour.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.parkour.api.model.parkour.Parkour
import dev.slne.surf.parkour.api.model.parkour.statistic.ParkourStatistic
import dev.slne.surf.parkour.core.service.ParkourStatisticsService
import net.kyori.adventure.util.Services
import org.jetbrains.exposed.sql.Table
import java.util.*

@AutoService(ParkourStatisticsService::class)
class FallbackParkourStatisticService : ParkourStatisticsService, Services.Fallback {
    object ParkourStatistics : Table("parkour_statistics") {
        val uuid
    }

    override fun createTable() {

    }

    override suspend fun getStatistic(uuid: UUID): ParkourStatistic {
        TODO("Not yet implemented")
    }

    override suspend fun getParkourStatistic(
        uuid: UUID,
        parkour: Parkour
    ): ParkourStatistic {
        TODO("Not yet implemented")
    }

    override suspend fun addStatistic(
        uuid: UUID,
        parkour: Parkour,
        statistic: ParkourStatistic
    ) {
        TODO("Not yet implemented")
    }
}