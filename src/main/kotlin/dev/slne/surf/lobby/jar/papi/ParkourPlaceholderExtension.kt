package dev.slne.surf.lobby.jar.papi

import dev.slne.surf.lobby.jar.mysql.Database
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unimi.dsi.fastutil.objects.ObjectList
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.util.*
import java.util.stream.Collectors

class ParkourPlaceholderExtension : PlaceholderExpansion() {
    override fun getIdentifier(): String {
        return "surf-lobby-parkour"
    }

    override fun getAuthor(): String {
        return "SLNE Development, TheBjoRedCraft"
    }

    override fun getVersion(): String {
        return "1.0.0"
    }

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
                return getName(place, sortedHighScores)
            } else if (params.endsWith("value")) {
                return getHighScore(place).toString()
            }
        } else if (category == "points") {
            if (params.endsWith("name")) {
                return getName(place, sortedPoints)
            } else if (params.endsWith("value")) {
                return getPoints(place).toString()
            }
        }

        return null
    }

    private fun getName(place: Int, sortedPlayers: ObjectList<UUID>): String? {
        if (place <= 0 || place > sortedPlayers.size) {
            return "/"
        }

        val uuid = sortedPlayers[place - 1]
        return getName(uuid)
    }

    private fun getHighScore(place: Int): Int {
        val sortedPlayers =
            sortedHighScores

        if (place <= 0 || place > sortedPlayers.size) {
            return -1
        }

        val uuid = sortedPlayers[place - 1]
        return Database.getHighScore(uuid) ?: -1
    }

    private fun getPoints(place: Int): Int {
        val sortedPlayers =
            sortedPoints

        if (place <= 0 || place > sortedPlayers.size) {
            return -1
        }

        val uuid = sortedPlayers[place - 1]
        return Database.getPoints(uuid) ?: -1
    }

    private fun getName(uuid: UUID): String? {
        val player = Bukkit.getOfflinePlayer(uuid)
        return if (player.name != null) player.name else "Unknown"
    }

    private val sortedHighScores: ObjectList<UUID>
        get() {
            val highScores = Database.highScores

            return highScores
                .entries
                .stream()
                .sorted(Comparator.comparingInt<Map.Entry<UUID?, Int?>> { obj: Map.Entry<UUID?, Int?> -> obj.value ?: 0 })
                .map<UUID?> { obj: Map.Entry<UUID?, Int?> -> obj.key }
                .collect(Collectors.toCollection<UUID?, ObjectArrayList<UUID>> { ObjectArrayList() })
        }

    private val sortedPoints: ObjectList<UUID>
        get() {
            val points = Database.points

            return points
                .entries
                .stream()
                .sorted(Comparator.comparingInt { obj: Map.Entry<UUID?, Int?> -> obj.value ?: 0 })
                .map<UUID?> { obj: Map.Entry<UUID?, Int?> -> obj.key }
                .collect(Collectors.toCollection<UUID?, ObjectArrayList<UUID>> { ObjectArrayList() })
        }
}
