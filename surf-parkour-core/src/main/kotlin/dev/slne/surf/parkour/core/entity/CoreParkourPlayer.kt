package dev.slne.surf.parkour.core.entity

import dev.slne.surf.parkour.api.entity.ParkourPlayer
import org.bukkit.Bukkit
import java.util.*

class CoreParkourPlayer(override val uuid: UUID, override val name: String) : ParkourPlayer {
    override fun player() = Bukkit.getPlayer(uuid)
    override fun offlinePlayer() = Bukkit.getOfflinePlayer(uuid)
}