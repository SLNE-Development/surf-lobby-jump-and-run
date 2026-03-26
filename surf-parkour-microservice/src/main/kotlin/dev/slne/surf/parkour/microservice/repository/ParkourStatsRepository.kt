package dev.slne.surf.parkour.microservice.repository

import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.*
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.insert
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.select
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.parkour.api.data.ParkourRun
import dev.slne.surf.parkour.api.data.ParkourStats
import dev.slne.surf.parkour.microservice.table.ParkourRunsTable
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import java.util.*

object ParkourStatsRepository {
    suspend fun fetchAllStats(): List<ParkourStats> = suspendTransaction {
        val totalRuns = ParkourRunsTable.id.count()
        val totalJumps = ParkourRunsTable.runJumps.sum()
        val highscore = ParkourRunsTable.runJumps.max()
        val averageTime = ParkourRunsTable.runTime.avg()

        ParkourRunsTable
            .select(
                ParkourRunsTable.playerUuid,
                totalRuns,
                totalJumps,
                highscore,
                averageTime
            )
            .groupBy(ParkourRunsTable.playerUuid)
            .map { row ->
                ParkourStats(
                    playerUuid = row[ParkourRunsTable.playerUuid],
                    totalRuns = row[totalRuns].toInt(),
                    totalJumps = row[totalJumps] ?: 0,
                    highscore = row[highscore] ?: 0,
                    averageTime = (row[averageTime] ?: 0.0).toLong()
                )
            }.toList()
    }

    suspend fun saveRun(parkourRun: ParkourRun) = suspendTransaction {
        ParkourRunsTable.insert {
            it[parkourUuid] = parkourRun.parkourUuid
            it[playerUuid] = parkourRun.playerUuid
            it[runJumps] = parkourRun.jumps
            it[runTime] = parkourRun.time
        }
    }

    suspend fun loadPlayerStats(playerUuid: UUID) = suspendTransaction {
        val totalRuns = ParkourRunsTable.id.count()
        val totalJumps = ParkourRunsTable.runJumps.sum()
        val highscore = ParkourRunsTable.runJumps.max()
        val averageTime = ParkourRunsTable.runTime.avg()

        ParkourRunsTable
            .select(
                ParkourRunsTable.playerUuid,
                totalRuns,
                totalJumps,
                highscore,
                averageTime
            )
            .where(ParkourRunsTable.playerUuid eq playerUuid)
            .groupBy(ParkourRunsTable.playerUuid)
            .map { row ->
                ParkourStats(
                    playerUuid = row[ParkourRunsTable.playerUuid],
                    totalRuns = row[totalRuns].toInt(),
                    totalJumps = row[totalJumps] ?: 0,
                    highscore = row[highscore] ?: 0,
                    averageTime = (row[averageTime] ?: 0.0).toLong()
                )
            }.firstOrNull()
    }
}