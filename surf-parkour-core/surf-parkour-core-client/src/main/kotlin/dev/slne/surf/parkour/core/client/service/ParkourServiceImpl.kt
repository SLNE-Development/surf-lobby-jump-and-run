package dev.slne.surf.parkour.core.client.service

import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.core.util.freeze
import dev.slne.surf.api.core.util.mutableObjectSetOf
import dev.slne.surf.parkour.api.data.PlayerTextures
import dev.slne.surf.parkour.core.client.config.ParkourConfig
import dev.slne.surf.parkour.core.client.config.ParkourConfiguration
import dev.slne.surf.parkour.core.client.model.geometry.ParkourLocation
import dev.slne.surf.parkour.core.client.model.geometry.ParkourRegion
import dev.slne.surf.parkour.core.client.model.parkour.Parkour
import dev.slne.surf.parkour.core.client.platform.ParkourPlatform
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import java.util.*

object ParkourServiceImpl : ParkourService {
    private val logger = ComponentLogger.logger("surf-parkour")

    private val _parkours = mutableObjectSetOf<Parkour>()
    override val parkours get() = _parkours.freeze()

    override fun createParkour(
        uuid: UUID,
        identifier: String,
        displayName: String,
        boundingBox: ParkourRegion,
        world: String,
        respawnLocation: ParkourLocation
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

    override fun triggerFailure(playerUuid: UUID) {
        val parkour = getParkourByPlayer(playerUuid) ?: return
        val audience = ParkourPlatform.audience(playerUuid)
        val playerName = ParkourPlatform.playerName(playerUuid)
        val skinTexture = ParkourPlatform.skinTexture(playerUuid) ?: ""

        parkour.preExit(playerUuid)

        audience?.sendText {
            appendInfoPrefix()
            info("Du bist runtergefallen...")
        }
        audience?.let { SoundService.playFailure(it) }

        parkour.processRun(playerUuid)?.let { highscore ->
            audience?.sendText {
                appendSuccessPrefix()
                success("Du hast mit ")
                variableValue(highscore)
                success(" Sprüngen einen neuen Highscore aufgestellt.")
            }
        }

        ParkourPlatform.launch {
            parkour.exit(playerUuid)

            if (playerName != null) {
                ParkourTexturesService.saveTexture(
                    PlayerTextures(
                        playerUuid,
                        playerName,
                        skinTexture
                    )
                )
            }
        }
    }

    override fun triggerSuccess(playerUuid: UUID) {
        val parkour = getParkourByPlayer(playerUuid) ?: return
        val generator = parkour.getGenerator(playerUuid) ?: return

        if (generator.advanced) {
            return
        }

        generator.advanced = true

        ParkourPlatform.launch {
            ParkourPlatform.audience(playerUuid)?.let { SoundService.playSuccess(it) }
            generator.generate()
        }
    }

    override fun isInParkour(playerUuid: UUID) = _parkours.any { it.players.contains(playerUuid) }

    override fun getParkourByPlayer(playerUuid: UUID) =
        _parkours.find { it.players.contains(playerUuid) }

    override fun getParkour(identifier: String) = _parkours.find { it.identifier == identifier }
    override fun getParkour(parkourUuid: UUID) = _parkours.find { it.uuid == parkourUuid }
    override fun exists(identifier: String) = _parkours.any { it.identifier == identifier }

    override fun loadParkours() {
        logger.info("Loading parkours, this should not take too long...")

        _parkours.clear()
        _parkours.addAll(ParkourConfig.getConfig().parkours.map { it.toParkour() })

        logger.info("Loaded ${_parkours.size} parkours!")
    }
}
