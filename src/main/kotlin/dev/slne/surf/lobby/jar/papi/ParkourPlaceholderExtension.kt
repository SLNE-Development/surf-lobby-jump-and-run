package dev.slne.surf.lobby.jar.papi

import dev.slne.surf.lobby.jar.service.JumpAndRunService
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.util.*

class ParkourPlaceholderExtension : PlaceholderExpansion() {
    override fun getIdentifier() = "surf-lobby-parkour"

    override fun getAuthor() = "SLNE Development, TheBjoRedCraft"

    override fun getVersion() = "1.0.0"

    override fun onRequest(player: OfflinePlayer, params: String): String? {
        val parts = params.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        if (parts.size < 3) {
            return null
        }

        val category = parts[0]
        val place: Int

        try {
            place = parts[1].toInt()
        } catch (e: NumberFormatException) {
            return null
        }

        if (category == "highscore") {
            if (params.endsWith("name")) {
                return getHighScoreName(place)
            } else if (params.endsWith("value")) {
                return getHighScore(place).toString()
            }
        } else if (category == "points") {
            if (params.endsWith("name")) {
                return getPointsName(place)
            } else if (params.endsWith("value")) {
                return getPoints(place).toString()
            }
        }

        return null
    }

    private fun getHighScoreName(index: Int): String {
        val list = JumpAndRunService.leaderboardHighscores.toList()

        if (index < 0 || index >= list.size) {
            return "Unknown"
        }

        return getName(list[index].first)
    }

    private fun getPointsName(index: Int): String {
        val list = JumpAndRunService.leaderboardPoints.toList()

        if (index < 0 || index >= list.size) {
            return "Unknown"
        }

        return getName(list[index].first)
    }

    private fun getHighScore(place: Int): Int {
        val list = JumpAndRunService.leaderboardHighscores.toList()

        if (place <= 0 || place > list.size) {
            return -1
        }

        return list[place - 1].second
    }

    private fun getPoints(place: Int): Int {
        val list = JumpAndRunService.leaderboardPoints.toList()

        if (place <= 0 || place > list.size) {
            return -1
        }

        return list[place - 1].second
    }

    private fun getName(uuid: UUID) = Bukkit.getOfflinePlayer(uuid).name ?: "Unknown"
}
