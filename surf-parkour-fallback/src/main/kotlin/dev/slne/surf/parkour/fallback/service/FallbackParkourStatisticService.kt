package dev.slne.surf.parkour.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.parkour.api.model.parkour.Parkour
import dev.slne.surf.parkour.api.model.parkour.statistic.ParkourStatistic
import dev.slne.surf.parkour.core.model.statistic.CoreParkourSpecificStatistic
import dev.slne.surf.parkour.core.service.ParkourStatisticsService
import dev.slne.surf.parkour.fallback.entity.ParkourStatisticEntity
import dev.slne.surf.parkour.fallback.table.ParkourStatisticsTable
import kotlinx.coroutines.Dispatchers
import net.kyori.adventure.util.Services
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

@AutoService(ParkourStatisticsService::class)
class FallbackParkourStatisticService : ParkourStatisticsService, Services.Fallback {
    override fun createTable() {
        transaction {
            SchemaUtils.create(ParkourStatisticsTable)
        }
    }

    override suspend fun getStatistic(uuid: UUID) = newSuspendedTransaction(Dispatchers.IO) {
        ParkourStatisticEntity.find(ParkourStatisticsTable.userUuid eq uuid).map {
            CoreParkourSpecificStatistic(
                it.parkour.it.userUuid,
                it.tries,
                it.failures,
                it.bestTry
            )
        }.firstOrNull() ?: ParkourStatistic(uuid, 0, 0, null)
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