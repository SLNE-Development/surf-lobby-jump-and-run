package dev.slne.surf.parkour.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.parkour.api.model.parkour.Parkour
import dev.slne.surf.parkour.api.model.parkour.statistic.ParkourStatistic
import dev.slne.surf.parkour.api.model.parkour.statistic.ParkourStatisticSummary
import dev.slne.surf.parkour.core.model.statistic.CoreParkourStatistic
import dev.slne.surf.parkour.core.model.statistic.CoreParkourStatisticSummary
import dev.slne.surf.parkour.core.service.ParkourStatisticsService
import dev.slne.surf.parkour.fallback.entity.ParkourEntity
import dev.slne.surf.parkour.fallback.entity.ParkourPlayerEntity
import dev.slne.surf.parkour.fallback.entity.ParkourStatisticEntity
import dev.slne.surf.parkour.fallback.table.ParkourPlayerTable
import dev.slne.surf.parkour.fallback.table.ParkourStatisticsTable
import dev.slne.surf.parkour.fallback.table.ParkourTable
import dev.slne.surf.surfapi.core.api.service.PlayerLookupService
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import it.unimi.dsi.fastutil.objects.ObjectSet
import kotlinx.coroutines.Dispatchers
import net.kyori.adventure.util.Services
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import kotlin.math.roundToInt

@AutoService(ParkourStatisticsService::class)
class FallbackParkourStatisticService : ParkourStatisticsService, Services.Fallback {
    override fun createTable() {
        transaction {
            SchemaUtils.create(ParkourStatisticsTable)
        }
    }

    override suspend fun getSummary(userUuid: UUID) = newSuspendedTransaction(Dispatchers.IO) {
        val statistics = ParkourStatisticEntity.find { ParkourStatisticsTable.userUuid eq userUuid }
            .map { it.toDto() }
        val totalCompletions = statistics.size
        val totalJumps = statistics.sumOf { it.jumps }

        val bestTime = statistics.maxByOrNull { it.time }?.time ?: 0L
        val worstTime = statistics.minByOrNull { it.time }?.time ?: 0L
        val averageTime = if (statistics.isNotEmpty()) {
            statistics.map { it.time }.average().roundToInt()
        } else 0

        val bestJumps = statistics.maxByOrNull { it.jumps }?.jumps ?: 0
        val worstJumps = statistics.minByOrNull { it.jumps }?.jumps ?: 0
        val averageJumps = if (statistics.isNotEmpty()) {
            statistics.map { it.jumps }.average().roundToInt()
        } else 0

        val name = PlayerLookupService.getUsername(userUuid) ?: "Error"

        CoreParkourStatisticSummary(
            name = name,
            uuid = userUuid,
            totalTries = totalCompletions,
            totalJumps = totalJumps,
            averageTime = averageTime,
            averageJumps = averageJumps,
            bestTime = bestTime.toInt(),
            bestJumps = bestJumps,
            worstTime = worstTime.toInt(),
            worstJumps = worstJumps
        )
    }

    override suspend fun getEverySummary(): ObjectSet<ParkourStatisticSummary> =
        newSuspendedTransaction(Dispatchers.IO) {
            ParkourStatisticEntity.all().groupBy { it.userUuid }.map { (user, stats) ->
                val totalCompletions = stats.size
                val totalJumps = stats.sumOf { it.jumps }

                val bestTime = stats.maxByOrNull { it.time }?.time ?: 0L
                val worstTime = stats.minByOrNull { it.time }?.time ?: 0L
                val averageTime = if (stats.isNotEmpty()) {
                    stats.map { it.time }.average().roundToInt()
                } else 0

                val bestJumps = stats.maxByOrNull { it.jumps }?.jumps ?: 0
                val worstJumps = stats.minByOrNull { it.jumps }?.jumps ?: 0
                val averageJumps = if (stats.isNotEmpty()) {
                    stats.map { it.jumps }.average().roundToInt()
                } else 0

                user to CoreParkourStatisticSummary(
                    name = user.name,
                    uuid = user.uuid,
                    totalTries = totalCompletions,
                    totalJumps = totalJumps,
                    averageTime = averageTime,
                    averageJumps = averageJumps,
                    bestTime = bestTime.toInt(),
                    bestJumps = bestJumps,
                    worstTime = worstTime.toInt(),
                    worstJumps = worstJumps
                )
            }.map { it.second }.toObjectSet()
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