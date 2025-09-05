package dev.slne.surf.parkour.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.parkour.api.model.parkour.Parkour
import dev.slne.surf.parkour.api.model.parkour.statistic.ParkourStatistic
import dev.slne.surf.parkour.core.model.CoreParkourStatistic
import dev.slne.surf.parkour.core.service.ParkourStatisticsService
import dev.slne.surf.parkour.fallback.entity.ParkourEntity
import dev.slne.surf.parkour.fallback.entity.ParkourPlayerEntity
import dev.slne.surf.parkour.fallback.entity.ParkourStatisticEntity
import dev.slne.surf.parkour.fallback.table.ParkourPlayerTable
import dev.slne.surf.parkour.fallback.table.ParkourStatisticsTable
import dev.slne.surf.parkour.fallback.table.ParkourTable
import kotlinx.coroutines.Dispatchers
import net.kyori.adventure.util.Services
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
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

    override suspend fun getStatistic(
        userUuid: UUID,
        parkour: Parkour
    ) = newSuspendedTransaction(Dispatchers.IO) {
        val parkourEntity = ParkourEntity.find { ParkourTable.uuid eq parkour.uuid }
            .firstOrNull() ?: return@newSuspendedTransaction CoreParkourStatistic.empty(
            userUuid,
            parkour
        )

        ParkourStatisticEntity.find {
            (ParkourStatisticsTable.parkourId eq parkourEntity.id) and
                    (ParkourStatisticsTable.userUuid eq userUuid)
        }.firstOrNull()?.toDto() ?: CoreParkourStatistic.empty(userUuid, parkour)
    }

    override suspend fun addStatistic(
        statistic: ParkourStatistic
    ) = newSuspendedTransaction(Dispatchers.IO) {
        ParkourStatisticEntity.new {
            val parkourEntity = ParkourEntity.find { ParkourTable.uuid eq statistic.parkour.uuid }
                .firstOrNull()
                ?: error("Parkour ${statistic.parkour.name} does not exist in database!")

            val playerEntity =
                ParkourPlayerEntity.find(ParkourPlayerTable.uuid eq statistic.userUuid)
                    .firstOrNull()
                    ?: error("Player with UUID ${statistic.userUuid} does not exist in database!")

            parkour = parkourEntity
            userUuid = playerEntity
            time = statistic.time
            jumps = statistic.jumps
        }
        return@newSuspendedTransaction
    }
}