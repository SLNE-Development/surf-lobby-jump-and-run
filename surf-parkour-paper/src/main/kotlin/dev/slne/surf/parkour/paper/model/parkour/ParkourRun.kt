package dev.slne.surf.parkour.paper.model.parkour

import org.bukkit.Bukkit
import java.util.*

data class ParkourRun(
    val parkour: Parkour,
    val playerUuid: UUID,
    var jumps: Int,
    var time: Long
) {
    val playerName by lazy {
        Bukkit.getOfflinePlayer(playerUuid).name ?: "#Unknown"
    }
}