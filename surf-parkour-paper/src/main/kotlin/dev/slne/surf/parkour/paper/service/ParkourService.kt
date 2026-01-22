package dev.slne.surf.parkour.paper.service

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.shynixn.mccoroutine.folia.launch
import com.sksamuel.aedile.core.expireAfterWrite
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.SortOrder
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.and
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.eq
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.deleteWhere
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.insert
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.selectAll
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.parkour.paper.config
import dev.slne.surf.parkour.paper.database.ParkourRunsTable
import dev.slne.surf.parkour.paper.database.ParkourTable
import dev.slne.surf.parkour.paper.menu.type.LeaderboardSortingType
import dev.slne.surf.parkour.paper.model.parkour.Parkour
import dev.slne.surf.parkour.paper.model.parkour.ParkourRun
import dev.slne.surf.parkour.paper.model.parkour.PersonalParkourSummary
import dev.slne.surf.parkour.paper.plugin
import dev.slne.surf.parkour.paper.util.formattedDuration
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import dev.slne.surf.surfapi.core.api.util.toMutableObjectList
import dev.slne.surf.surfapi.core.api.util.toObjectList
import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.toList
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.util.BoundingBox
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.minutes

class ParkourService {
    private val _parkours = mutableObjectSetOf<Parkour>()
    private val summaryCache =
        Caffeine.newBuilder()
            .expireAfterWrite(30.minutes)
            .build<Pair<LeaderboardSortingType, Int>, List<PersonalParkourSummary>>()

    private val loadingPages = mutableSetOf<Pair<LeaderboardSortingType, Int>>()

    fun createParkour(
        uuid: UUID,
        identifier: String,
        displayName: String,
        boundingBox: BoundingBox,
        world: World,
        respawnLocation: Location
    ): Parkour {
        val parkour = Parkour(
            uuid = uuid,
            identifier = identifier,
            displayName = displayName,
            boundingBox = boundingBox,
            world = world,
            respawnLocation = respawnLocation
        )

        _parkours.add(parkour)

        plugin.launch {
            registerParkour(config.serverUuid, parkour)
        }

        return parkour
    }

    suspend fun triggerFailure(player: Player) {
        val parkour = getParkour(player) ?: return

        parkour.preExit(player.uniqueId)

        player.sendText {
            appendInfoPrefix()
            info("Du bist runtergefallen...")
        }
        soundService.playFailure(player)

        parkour.processRun(player.uniqueId)?.let {
            player.sendText {
                appendSuccessPrefix()
                success("Du hast mit ")
                variableValue(it)
                success(" Sprüngen einen neuen Highscore aufgestellt.")
            }
        }
        parkour.exit(player.uniqueId)
    }

    suspend fun triggerSuccess(player: Player) {
        val parkour = getParkour(player) ?: return
        val generator = parkour.getGenerator(player) ?: return

        generator.generate()
        soundService.playSuccess(player)
    }

    fun isInParkour(playerUuid: UUID) = _parkours.any { it.players.contains(playerUuid) }

    fun getParkour(player: Player) = _parkours.find { it.players.contains(player.uniqueId) }
    fun getParkours() = _parkours
    fun getParkour(identifier: String) = _parkours.find { it.identifier == identifier }
    fun getParkour(uuid: UUID) = _parkours.find { it.uuid == uuid }
    fun exists(identifier: String) = _parkours.any { it.identifier == identifier }

    suspend fun addRun(run: ParkourRun) = suspendTransaction {
        ParkourRunsTable.insert {
            it[parkourUuid] = run.parkour.uuid
            it[playerUuid] = run.playerUuid
            it[runJumps] = run.jumps
            it[runTime] = run.time
        }
    }

    suspend fun getRuns(player: UUID) =
        suspendTransaction {
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
            }.toList().toObjectList()
        }

    suspend fun getHighscore(player: UUID, parkour: Parkour) =
        suspendTransaction {
            ParkourRunsTable
                .selectAll()
                .where(
                    (ParkourRunsTable.parkourUuid eq parkour.uuid) and
                            (ParkourRunsTable.playerUuid eq player)
                )
                .orderBy(ParkourRunsTable.runJumps to SortOrder.ASC)
                .limit(1)
                .firstOrNull()
                ?.let {
                    ParkourRun(
                        playerUuid = player,
                        parkour = parkour,
                        jumps = it[ParkourRunsTable.runJumps],
                        time = it[ParkourRunsTable.runTime]
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

    suspend fun unregisterParkour(serverUuid: UUID, parkour: Parkour) =
        suspendTransaction {
            ParkourTable.deleteWhere {
                (parkourUuid eq parkour.uuid) and (this.serverUuid eq serverUuid)
            }
        }

    suspend fun getParkours(serverUUid: UUID) = suspendTransaction {
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

    fun loadParkours() {
        val serverUuid = config.serverUuid

        plugin.launch {
            val parkours = getParkours(serverUuid)

            _parkours.clear()
            _parkours.addAll(parkours)
        }
    }

    fun getSummaryPage(
        sorting: LeaderboardSortingType,
        page: Int
    ) = summaryCache.getIfPresent(sorting to page)

    fun loadSummaryPage(
        sorting: LeaderboardSortingType,
        page: Int,
        pageSize: Int,
        onLoaded: (List<PersonalParkourSummary>) -> Unit = {}
    ) {
        val key = sorting to page

        if (summaryCache.getIfPresent(key) != null) return
        if (!loadingPages.add(key)) return

        plugin.launch {
            val data = loadSummaryPage(sorting, page, pageSize)

            summaryCache.put(key, data)
            loadingPages.remove(key)

            val nextPage = sorting to (page + 1)
            if (summaryCache.getIfPresent(nextPage) == null) {
                loadSummaryPage(sorting, page + 1, pageSize)
            }

            onLoaded(data)
        }
    }


    private suspend fun loadSummaryPage(
        sorting: LeaderboardSortingType,
        page: Int,
        pageSize: Int
    ): List<PersonalParkourSummary> = suspendTransaction {
        val offset = page * pageSize
        val stats = ParkourRunsTable
            .selectAll()
            .toList()
            .groupBy { it[ParkourRunsTable.playerUuid] }
            .map { (player, rows) ->
                PersonalParkourSummary(
                    uuid = player,
                    runs = mutableObjectListOf(
                        rows.map {
                            ParkourRun(
                                playerUuid = player,
                                parkour = getParkour(it[ParkourRunsTable.parkourUuid])!!,
                                jumps = it[ParkourRunsTable.runJumps],
                                time = it[ParkourRunsTable.runTime]
                            )
                        }
                    )
                )
            }.toMutableObjectList()

        sorting.sort(stats)
        stats.drop(offset).take(pageSize)
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
                            darkSpacer("»")
                            appendSpace()
                            variableKey("Sprünge:")
                            appendSpace()
                            variableValue(it.currentIndex)
                            appendSpace()
                            spacer("|")
                            appendSpace()
                            variableKey("Zeit:")
                            appendSpace()
                            variableValue((System.currentTimeMillis() - it.startTime).formattedDuration)
                            appendSpace()
                            darkSpacer("«")
                        })
                    }
                }
            }, 0L, 500L, TimeUnit.MILLISECONDS)
        }

        fun stopUpdating() {
            if (::updateTask.isInitialized && !updateTask.isCancelled) {
                updateTask.cancel()
            }
        }
    }
}

val parkourService get() = ParkourService.INSTANCE