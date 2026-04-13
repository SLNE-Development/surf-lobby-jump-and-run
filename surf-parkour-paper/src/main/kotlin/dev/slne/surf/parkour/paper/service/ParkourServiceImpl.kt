package dev.slne.surf.parkour.paper.service

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.core.util.freeze
import dev.slne.surf.api.core.util.mutableObjectSetOf
import dev.slne.surf.parkour.api.data.PlayerTextures
import dev.slne.surf.parkour.core.paper.service.ParkourTexturesService
import dev.slne.surf.parkour.paper.config.ParkourConfig
import dev.slne.surf.parkour.paper.config.ParkourConfiguration
import dev.slne.surf.parkour.paper.model.parkour.Parkour
import dev.slne.surf.parkour.paper.plugin
import dev.slne.surf.parkour.paper.util.formattedDuration
import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.util.BoundingBox
import java.util.*
import java.util.concurrent.TimeUnit

object ParkourServiceImpl : ParkourService {
    private val _parkours = mutableObjectSetOf<Parkour>()
    override val parkours get() = _parkours.freeze()

    override fun createParkour(
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

        ParkourConfiguration.edit {
            parkours.add(ParkourConfig.fromParkour(parkour))
        }

        return parkour
    }

    override fun triggerFailure(player: Player) {
        val parkour = getParkour(player) ?: return

        parkour.preExit(player.uniqueId)

        player.sendText {
            appendInfoPrefix()
            info("Du bist runtergefallen...")
        }
        SoundService.playFailure(player)

        parkour.processRun(player.uniqueId)?.let {
            player.sendText {
                appendSuccessPrefix()
                success("Du hast mit ")
                variableValue(it)
                success(" Sprüngen einen neuen Highscore aufgestellt.")
            }
        }

        plugin.launch {
            parkour.exit(player.uniqueId)

            ParkourTexturesService.saveTexture(
                PlayerTextures(
                    player.uniqueId,
                    player.name,
                    player.playerProfile.properties.find { it.name == "textures" }?.value ?: ""
                )
            )
        }
    }

    override fun triggerSuccess(player: Player) {
        val parkour = getParkour(player) ?: return
        val generator = parkour.getGenerator(player) ?: return

        if (generator.advanced) {
            return
        }

        generator.advanced = true

        plugin.launch {
            SoundService.playSuccess(player)
            generator.generate()
        }
    }

    override fun isInParkour(playerUuid: UUID) = _parkours.any { it.players.contains(playerUuid) }
    override fun isInParkour(player: Player) = isInParkour(player.uniqueId)

    override fun getParkour(player: Player) =
        _parkours.find { it.players.contains(player.uniqueId) }

    override fun getParkour(identifier: String) = _parkours.find { it.identifier == identifier }
    override fun getParkour(playerUuid: UUID) = _parkours.find { it.uuid == playerUuid }
    override fun exists(identifier: String) = _parkours.any { it.identifier == identifier }

    override fun loadParkours() {
        plugin.logger.info("Loading parkours, this should not take too long...")

        _parkours.clear()
        _parkours.addAll(ParkourConfig.getConfig().parkours.map { it.toParkour() })

        plugin.logger.info("Loaded ${_parkours.size} parkours!")
    }

    private lateinit var updateTask: ScheduledTask

    fun startUpdating() {
        if (::updateTask.isInitialized && !updateTask.isCancelled) {
            return
        }

        updateTask = Bukkit.getAsyncScheduler().runAtFixedRate(plugin, {
            parkours.forEach { pkr ->
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