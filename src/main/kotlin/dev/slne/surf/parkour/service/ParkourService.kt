package dev.slne.surf.parkour.service

import dev.slne.surf.parkour.`object`.parkour.Parkour
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import org.bukkit.entity.Player

class ParkourService {
    private val _parkours = mutableObjectSetOf<Parkour>()

    suspend fun startParkour(player: Player, parkour: Parkour) {
        parkour.start(player.uniqueId)
    }

    fun createParkour(
        identifier: String,
        displayName: String,
        boundingBox: org.bukkit.util.BoundingBox,
        world: org.bukkit.World,
        startLocation: org.bukkit.Location,
        respawnLocation: org.bukkit.Location
    ): Parkour {
        val parkour = Parkour(
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

    suspend fun addRun()

    companion object {
        val INSTANCE = ParkourService()
    }
}

val parkourService get() = ParkourService.INSTANCE