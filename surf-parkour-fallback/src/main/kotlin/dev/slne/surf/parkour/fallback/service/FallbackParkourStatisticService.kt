package dev.slne.surf.parkour.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.parkour.api.model.parkour.Parkour
import dev.slne.surf.parkour.api.model.parkour.statistic.ParkourStatistic
import dev.slne.surf.parkour.core.model.statistic.CoreParkourStatistic
import dev.slne.surf.parkour.core.service.ParkourStatisticsService
import dev.slne.surf.parkour.fallback.entity.ParkourEntity
import dev.slne.surf.parkour.fallback.entity.ParkourStatisticEntity
import dev.slne.surf.parkour.fallback.table.ParkourStatisticsTable
import dev.slne.surf.parkour.fallback.table.ParkourTable
import kotlinx.coroutines.Dispatchers
import net.kyori.adventure.util.Services
import org.jetbrains.exposed.sql.SchemaUtils
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
        val parkourEntity = ParkourEntity.find { ParkourTable.name eq parkour.name }
            .firstOrNull() ?: return@newSuspendedTransaction CoreParkourStatistic.empty()

        ParkourStatisticEntity.find {
            (ParkourStatisticsTable.parkourId eq parkourEntity.id) and
                    (ParkourStatisticsTable.userUuid eq userUuid)
        }.firstOrNull()?.toDto() ?: CoreParkourStatistic.empty()
    }

    override suspend fun addStatistic(
        userUuid: UUID,
        parkour: Parkour,
        statistic: ParkourStatistic
    ) = newSuspendedTransaction(Dispatchers.IO) {
        val parkourEntity = ParkourEntity.find { ParkourTable.name eq parkour.name }
            .firstOrNull() ?: error("Parkour ${parkour.name} not found")

        val existing = ParkourStatisticEntity.find {
            (ParkourStatisticsTable.parkourId eq parkourEntity.id) and
                    (ParkourStatisticsTable.userUuid eq userUuid)
        }.firstOrNull()

        if (existing != null) {
            existing.tries = statistic.tries
            existing.failures = statistic.failures
            existing.bestTry = statistic.bestTry
            existing.overallJumps = statistic.overallJumps
            return@newSuspendedTransaction
        }

        ParkourStatisticEntity.new {
            this.parkour = parkourEntity
            this.userUuid = userUuid
            this.tries = statistic.tries
            this.failures = statistic.failures
            this.bestTry = statistic.bestTry
            this.overallJumps = statistic.overallJumps
        }
    }
}