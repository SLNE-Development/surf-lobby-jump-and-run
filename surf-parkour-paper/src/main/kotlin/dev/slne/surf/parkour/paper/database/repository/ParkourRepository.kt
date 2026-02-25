package dev.slne.surf.parkour.paper.database.repository

import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.*
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.deleteWhere
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.insert
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.select
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.selectAll
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.parkour.api.data.ParkourStats
import dev.slne.surf.parkour.paper.database.table.ParkourRunsTable
import dev.slne.surf.parkour.paper.database.table.ParkourTable
import dev.slne.surf.parkour.paper.model.parkour.Parkour
import dev.slne.surf.parkour.paper.model.parkour.ParkourRun
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import it.unimi.dsi.fastutil.objects.ObjectSet
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.toSet
import java.util.*

val parkourRepository = ParkourRepository()

class ParkourRepository {
    suspend fun fetchAllStats(): ObjectSet<ParkourStats> = suspendTransaction {
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
            }
            .toSet()
            .toObjectSet()
    }

    suspend fun saveRun(parkourRun: ParkourRun) = suspendTransaction {
        ParkourRunsTable.insert {
            it[parkourUuid] = parkourRun.parkour.uuid
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
            }
    }

    suspend fun registerParkour(serverUuid: UUID, parkour: Parkour) =
        suspendTransaction {
            ParkourTable.insert {
                it[parkourUuid] = parkour.uuid
                it[this.serverUuid] = serverUuid
                it[identifier] = parkour.identifier
                it[displayName] = parkour.displayName
                it[world] = parkour.world
                it[boundingBox] = parkour.boundingBox
                it[respawnLocation] = parkour.respawnLocation
            }
        }

    suspend fun loadParkours(serverUUid: UUID) = suspendTransaction {
        ParkourTable.selectAll().where(
            (ParkourTable.serverUuid eq serverUUid)
        ).map {
            Parkour(
                uuid = it[ParkourTable.parkourUuid],
                identifier = it[ParkourTable.identifier],
                displayName = it[ParkourTable.displayName],
                boundingBox = it[ParkourTable.boundingBox],
                world = it[ParkourTable.world],
                respawnLocation = it[ParkourTable.respawnLocation]
            )
        }.toList()
    }

    suspend fun deleteParkour(serverUuid: UUID, parkour: Parkour) =
        suspendTransaction {
            ParkourTable.deleteWhere {
                (parkourUuid eq parkour.uuid) and (this.serverUuid eq serverUuid)
            }
        }
}