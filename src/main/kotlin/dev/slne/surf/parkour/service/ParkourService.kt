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

    fun isInParkour(player: Player) = getParkour(player) != null

    suspend fun triggerFailure(player: Player) {
        val parkour = getParkour(player) ?: return
        parkour.exit(player.uniqueId)

        player.sendText {
            appendPrefix()
            success("failed")
        }
    }

    suspend fun triggerSuccess(player: Player) {
        val parkour = getParkour(player) ?: return

        player.sendText {
            appendPrefix()
            success("completed the parkour")
        }
    }

    fun getParkour(player: Player) = _parkours.find { it.players.contains(player.uniqueId) }

    companion object {
        val INSTANCE = ParkourService()
    }
}

val parkourService get() = ParkourService.INSTANCE