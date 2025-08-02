package dev.slne.surf.parkour.core.factory

import dev.slne.surf.parkour.api.entity.ParkourPlayer
import dev.slne.surf.surfapi.core.api.util.requiredService
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.*

interface ParkourPlayerFactory {
    fun createPlayer(uuid: UUID, name: String): ParkourPlayer
    fun from(player: Player): ParkourPlayer
    fun from(offlinePlayer: OfflinePlayer): ParkourPlayer
    fun from(name: String): ParkourPlayer?
    fun from(uuid: UUID): ParkourPlayer?

    companion object {
        val INSTANCE = requiredService<ParkourPlayerFactory>()
    }
}

val parkourPlayerFactory get() = ParkourPlayerFactory.INSTANCE