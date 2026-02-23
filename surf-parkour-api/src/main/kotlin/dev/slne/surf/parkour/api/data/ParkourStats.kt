package dev.slne.surf.parkour.api.data

import org.bukkit.Bukkit
import java.util.*

data class ParkourStats(
    val playerUuid: UUID,
    val totalRuns: Int,
    val totalJumps: Int,
    val highscore: Int,
    val averageTime: Long,
) {
    companion object {
        fun empty() = ParkourStats(
            playerUuid = UUID.randomUUID(),
            totalRuns = -1,
            totalJumps = -1,
            highscore = -1,
            averageTime = -1L
        )
    }

    val playerName by lazy {
        Bukkit.getOfflinePlayer(playerUuid).name ?: "#Unknown"
    }
}