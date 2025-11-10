package dev.slne.surf.parkour.service

import dev.slne.surf.parkour.database.ParkourRunsTable
import dev.slne.surf.parkour.`object`.parkour.Parkour
import dev.slne.surf.parkour.`object`.parkour.ParkourRun
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import kotlinx.coroutines.Dispatchers
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
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
        uuid: UUID = UUID.randomUUID(),
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
        return parkour
    }

    fun isInParkour(player: Player) = getParkour(player) != null

    suspend fun triggerFailure(player: Player) {
        val parkour = getParkour(player) ?: return
        parkour.exit(player.uniqueId)

        player.sendText {
            appendPrefix()
            error("Der Parkour wurde abgebrochen.")
        }
        soundService.playFailure(player)
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
    fun exists(identifier: String) = _parkours.any { it.identifier == identifier }

    suspend fun addRun(run: ParkourRun) = newSuspendedTransaction(Dispatchers.IO) {
        ParkourRunsTable.insert {
            it[parkourUuid] = run.parkour.uuid
            it[playerUuid] = run.playerUuid
            it[runJumps] = run.jumps
            it[runTime] = run.time
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

    companion object {
        val INSTANCE = ParkourService()
    }
}

val parkourService get() = ParkourService.INSTANCE