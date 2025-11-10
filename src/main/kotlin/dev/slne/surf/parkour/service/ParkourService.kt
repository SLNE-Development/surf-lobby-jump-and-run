package dev.slne.surf.parkour.service

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.parkour.database.ParkourRunsTable
import dev.slne.surf.parkour.database.ParkourTable
import dev.slne.surf.parkour.`object`.parkour.Parkour
import dev.slne.surf.parkour.`object`.parkour.ParkourRun
import dev.slne.surf.parkour.parkourConfig
import dev.slne.surf.parkour.plugin
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import kotlinx.coroutines.Dispatchers
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.*

class ParkourService {
    private val _parkours = mutableObjectSetOf<Parkour>()

    suspend fun startParkour(player: Player, parkour: Parkour) {
        parkour.start(player.uniqueId)
    }

    fun createParkour(
        uuid: UUID,
        identifier: String,
        displayName: String,
        boundingBox: org.bukkit.util.BoundingBox,
        world: org.bukkit.World,
        startLocation: org.bukkit.Location,
        respawnLocation: org.bukkit.Location
    ): Parkour {
        val parkour = Parkour(
            uuid = uuid,
            identifier = identifier,
            displayName = displayName,
            boundingBox = boundingBox,
            world = world,
            startLocation = startLocation,
            respawnLocation = respawnLocation
        )

        _parkours.add(parkour)

        plugin.launch {
            registerParkour(parkourConfig.config.serverUuid, parkour)
        }

        return parkour
    }

    fun isInParkour(player: Player) = getParkour(player) != null

    suspend fun triggerFailure(player: Player) {
        val parkour = getParkour(player) ?: return

        soundService.playFailure(player)

        parkour.processRun(player.uniqueId)
        parkour.exit(player.uniqueId)
    }

    suspend fun triggerSuccess(player: Player) {
        val parkour = getParkour(player) ?: return
        val generator = parkour.getGenerator(player) ?: return

        generator.generate()
        soundService.playSuccess(player)
    }

    fun getParkour(player: Player) = _parkours.find { it.players.contains(player.uniqueId) }
    fun getParkours() = _parkours
    fun getParkour(identifier: String) = _parkours.find { it.identifier == identifier }
    fun getParkour(uuid: UUID) = _parkours.find { it.uuid == uuid }
    fun exists(identifier: String) = _parkours.any { it.identifier == identifier }

    suspend fun addRun(run: ParkourRun) = newSuspendedTransaction(Dispatchers.IO) {
        ParkourRunsTable.insert {
            it[parkourUuid] = run.parkour.uuid
            it[playerUuid] = run.playerUuid
            it[runJumps] = run.jumps
            it[runTime] = run.time
        }
    }

    suspend fun getRuns(player: Player) =
        newSuspendedTransaction(Dispatchers.IO) {
            ParkourRunsTable.selectAll().where(
                (ParkourRunsTable.playerUuid eq player.uniqueId)
            ).mapNotNull { row ->
                val parkour =
                    getParkour(row[ParkourRunsTable.parkourUuid]) ?: return@mapNotNull null
                ParkourRun(
                    playerUuid = player.uniqueId,
                    parkour = parkour,
                    jumps = row[ParkourRunsTable.runJumps],
                    time = row[ParkourRunsTable.runTime]
                )
            }
        }

    suspend fun getRuns(player: Player, parkour: Parkour) =
        newSuspendedTransaction(Dispatchers.IO) {
            ParkourRunsTable.selectAll().where(
                (ParkourRunsTable.parkourUuid eq parkour.uuid) and
                        (ParkourRunsTable.playerUuid eq player.uniqueId)
            ).map {
                ParkourRun(
                    playerUuid = player.uniqueId,
                    parkour = parkour,
                    jumps = it[ParkourRunsTable.runJumps],
                    time = it[ParkourRunsTable.runTime]
                )
            }
        }

    suspend fun getHighscore(player: Player, parkour: Parkour) =
        newSuspendedTransaction(Dispatchers.IO) {
            ParkourRunsTable.selectAll().where(
                (ParkourRunsTable.parkourUuid eq parkour.uuid) and
                        (ParkourRunsTable.playerUuid eq player.uniqueId)
            ).orderBy(ParkourRunsTable.runTime).limit(1).firstNotNullOfOrNull {
                ParkourRun(
                    playerUuid = player.uniqueId,
                    parkour = parkour,
                    jumps = it[ParkourRunsTable.runJumps],
                    time = it[ParkourRunsTable.runTime]
                )
            }
        }

    suspend fun getHighscore(parkour: Parkour) =
        newSuspendedTransaction(Dispatchers.IO) {
            ParkourRunsTable.selectAll().where(
                (ParkourRunsTable.parkourUuid eq parkour.uuid)
            ).orderBy(ParkourRunsTable.runTime).limit(1).firstNotNullOfOrNull {
                ParkourRun(
                    playerUuid = it[ParkourRunsTable.playerUuid],
                    parkour = parkour,
                    jumps = it[ParkourRunsTable.runJumps],
                    time = it[ParkourRunsTable.runTime]
                )
            }
        }

    suspend fun registerParkour(serverUuid: UUID, parkour: Parkour) =
        newSuspendedTransaction(Dispatchers.IO) {
            ParkourTable.insert {
                it[parkourUuid] = parkour.uuid
                it[this.serverUuid] = serverUuid
                it[identifier] = parkour.identifier
                it[displayName] = parkour.displayName
                it[world] = parkour.world
                it[boundingBox] = parkour.boundingBox
                it[startLocation] = parkour.startLocation
                it[respawnLocation] = parkour.respawnLocation
            }
        }

    suspend fun unregisterParkour(serverUuid: UUID, parkour: Parkour) =
        newSuspendedTransaction(Dispatchers.IO) {
            ParkourTable.deleteWhere {
                (parkourUuid eq parkour.uuid) and (this.serverUuid eq serverUuid)
            }
        }

    suspend fun getParkours(serverUUid: UUID) = newSuspendedTransaction(Dispatchers.IO) {
        ParkourTable.selectAll().where(
            (ParkourTable.serverUuid eq serverUUid)
        ).map {
            Parkour(
                uuid = it[ParkourTable.parkourUuid],
                identifier = it[ParkourTable.identifier],
                displayName = it[ParkourTable.displayName],
                boundingBox = it[ParkourTable.boundingBox],
                world = it[ParkourTable.world],
                startLocation = it[ParkourTable.startLocation],
                respawnLocation = it[ParkourTable.respawnLocation]
            )
        }
    }

    fun loadParkours() {
        val serverUuid = parkourConfig.config.serverUuid

        plugin.launch {
            val parkours = getParkours(serverUuid)

            _parkours.clear()
            _parkours.addAll(parkours)
        }
    }

    companion object {
        val INSTANCE = ParkourService()
    }
}

val parkourService get() = ParkourService.INSTANCE