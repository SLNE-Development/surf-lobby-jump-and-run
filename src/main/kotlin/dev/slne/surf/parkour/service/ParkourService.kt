package dev.slne.surf.parkour.service

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.parkour.config
import dev.slne.surf.parkour.database.ParkourRunsTable
import dev.slne.surf.parkour.database.ParkourTable
import dev.slne.surf.parkour.model.parkour.Parkour
import dev.slne.surf.parkour.model.parkour.ParkourRun
import dev.slne.surf.parkour.plugin
import dev.slne.surf.parkour.util.formattedTimeEpoch
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import kotlinx.coroutines.Dispatchers
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.util.BoundingBox
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.*
import java.util.concurrent.TimeUnit

class ParkourService {
    private val _parkours = mutableObjectSetOf<Parkour>()

    fun createParkour(
        uuid: UUID,
        identifier: String,
        displayName: String,
        boundingBox: BoundingBox,
        world: World,
        startLocation: Location,
        respawnLocation: Location
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
            registerParkour(config.serverUuid, parkour)
        }

        return parkour
    }

    fun isInParkour(player: Player) = getParkour(player) != null

    suspend fun triggerFailure(player: Player) {
        val parkour = getParkour(player) ?: return

        parkour.preExit(player.uniqueId)
        soundService.playFailure(player)

        parkour.processRun(player.uniqueId)?.let {
            player.sendText {
                appendPrefix()
                success("Du hast mit ")
                variableValue(it)
                success(" Sprüngen einen neuen Highscore aufgestellt.")
            }
        }
        parkour.exit(player.uniqueId)

        player.sendText {
            appendPrefix()
            info("Du bist runtergefallen...")
        }
    }

    suspend fun triggerSuccess(player: Player) {
        val parkour = getParkour(player) ?: return
        val generator = parkour.getGenerator(player) ?: return

        generator.generate()

        val highscoreJumps = parkourService.getHighscore(player.uniqueId, parkour)?.jumps ?: 0
        val currentJumps = generator.currentIndex

        if (currentJumps == highscoreJumps + 1) {
            soundService.playHighscore(player)
        } else {
            soundService.playSuccess(player)
        }
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

    suspend fun getRuns(player: UUID) =
        newSuspendedTransaction(Dispatchers.IO) {
            ParkourRunsTable.selectAll().where(
                (ParkourRunsTable.playerUuid eq player)
            ).mapNotNull { row ->
                val parkour =
                    getParkour(row[ParkourRunsTable.parkourUuid]) ?: return@mapNotNull null
                ParkourRun(
                    playerUuid = player,
                    parkour = parkour,
                    jumps = row[ParkourRunsTable.runJumps],
                    time = row[ParkourRunsTable.runTime]
                )
            }
        }

    suspend fun getRuns(player: UUID, parkour: Parkour) =
        newSuspendedTransaction(Dispatchers.IO) {
            ParkourRunsTable.selectAll().where(
                (ParkourRunsTable.parkourUuid eq parkour.uuid) and
                        (ParkourRunsTable.playerUuid eq player)
            ).map {
                ParkourRun(
                    playerUuid = player,
                    parkour = parkour,
                    jumps = it[ParkourRunsTable.runJumps],
                    time = it[ParkourRunsTable.runTime]
                )
            }
        }

    suspend fun getHighscore(player: UUID, parkour: Parkour) =
        newSuspendedTransaction(Dispatchers.IO) {
            ParkourRunsTable.selectAll().where(
                (ParkourRunsTable.parkourUuid eq parkour.uuid) and
                        (ParkourRunsTable.playerUuid eq player)
            ).orderBy(ParkourRunsTable.runTime).limit(1).firstNotNullOfOrNull {
                ParkourRun(
                    playerUuid = player,
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
        val serverUuid = config.serverUuid

        plugin.launch {
            val parkours = getParkours(serverUuid)

            _parkours.clear()
            _parkours.addAll(parkours)
        }
    }

    companion object {
        val INSTANCE = ParkourService()

        private lateinit var updateTask: ScheduledTask

        fun startUpdating() {
            if (::updateTask.isInitialized && !updateTask.isCancelled) {
                return
            }

            updateTask = Bukkit.getAsyncScheduler().runAtFixedRate(plugin, {
                parkourService.getParkours().forEach { pkr ->
                    pkr.generators.forEach {
                        val player = Bukkit.getPlayer(it.associatedPlayer) ?: return@forEach

                        player.sendActionBar(buildText {
                            darkSpacer(">")
                            appendSpace()
                            variableKey("Sprünge:")
                            appendSpace()
                            variableValue(it.currentIndex)
                            appendSpace()
                            spacer("|")
                            appendSpace()
                            variableKey("Zeit:")
                            appendSpace()
                            variableValue((System.currentTimeMillis() - it.startTime).formattedTimeEpoch)
                            appendSpace()
                            darkSpacer("<")
                        })
                    }
                }
            }, 0L, 1L, TimeUnit.SECONDS)
        }

        fun stopUpdating() {
            if (::updateTask.isInitialized && !updateTask.isCancelled) {
                updateTask.cancel()
            }
        }
    }
}

val parkourService get() = ParkourService.INSTANCE