package dev.slne.surf.parkour.paper.service

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.parkour.api.data.ParkourStats
import dev.slne.surf.parkour.paper.config
import dev.slne.surf.parkour.paper.database.repository.parkourRepository
import dev.slne.surf.parkour.paper.model.parkour.Parkour
import dev.slne.surf.parkour.paper.plugin
import dev.slne.surf.parkour.paper.util.formattedDuration
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.util.BoundingBox
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

class ParkourService {
    private val _parkours = mutableObjectSetOf<Parkour>()
    private val statsCache = Caffeine.newBuilder().build<UUID, ParkourStats>()

    val stats get() = statsCache.asMap().values

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
            parkourRepository.registerParkour(config.serverUuid, parkour)
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

        parkour.processRun(player.uniqueId, player.name)?.let {
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

    suspend fun loadParkours() {
        val serverUuid = config.serverUuid
        val parkours = parkourRepository.loadParkours(serverUuid)

        _parkours.clear()
        _parkours.addAll(parkours)
    }

    fun getStats(playerUuid: UUID) = statsCache.getIfPresent(playerUuid) ?: ParkourStats.empty()

    suspend fun loadStats() {
        plugin.logger.info("Loading parkour stats, this may take a while...")

        val ms = measureTimeMillis {
            val stats = parkourRepository.fetchAllStats()
            stats.forEach { statsCache.put(it.playerUuid, it) }
        }

        plugin.logger.info("Loaded stats for ${statsCache.asMap().size} players in ${ms}ms!")
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