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
    companion object {
        private const val IDENTIFIER = "surf-parkour"
        private const val AUTHOR = "SLNE Development, TheBjoRedCraft"
        private const val VERSION = "1.0.0"
        private const val CATEGORY_HIGHSCORE = "highscore"
        private const val CATEGORY_POINTS = "points"
        private const val SUFFIX_NAME = "name"
        private const val SUFFIX_VALUE = "value"
    }
    
    /*
    Um die Platzhalter abzufragen, kannst du die folgenden Beispiele verwenden:  
    Um den Namen des Spielers mit dem höchsten Highscore abzufragen:  
    %surf-parkour_highscore_1_name%
    
    Um den Wert des höchsten Highscores abzufragen:  
    %surf-parkour_highscore_1_value%
    
    Um den Namen des Spielers mit den meisten Punkten abzufragen:  
    %surf-parkour_points_1_name%
    
    Um den Wert der meisten Punkte abzufragen:  
    %surf-parkour_points_1_value%
     */

    override fun getIdentifier(): String = IDENTIFIER
    override fun getAuthor(): String = AUTHOR
    override fun getVersion(): String = VERSION

    override fun onRequest(player: OfflinePlayer, params: String): String? {
        val parts = params.split("_")
        if (parts.size < 3) return null

        val category = parts[0]
        val place = parts[1].toIntOrNull() ?: return null
        val suffix = parts[2]

        return when (category) {
            CATEGORY_HIGHSCORE -> handleRequest(place, suffix, ::getHighScore, ::getSortedHighScores)
            CATEGORY_POINTS -> handleRequest(place, suffix, ::getPoints, ::getSortedPoints)
            else -> null
        }
    }

    private fun handleRequest(
        place: Int,
        suffix: String,
        valueProvider: (Int) -> Int,
        sortedPlayersProvider: () -> ObjectList<UUID>
    ): String? {
        return when (suffix) {
            SUFFIX_NAME -> getName(place, sortedPlayersProvider())
            SUFFIX_VALUE -> valueProvider(place).toString()
            else -> null
        }
    }

    private fun getName(place: Int, sortedPlayers: ObjectList<UUID>): String {
        if (place <= 0 || place > sortedPlayers.size) return "/"
        val uuid = sortedPlayers[place - 1]
        return getName(uuid)
    }

    private fun getHighScore(place: Int): Int {
        val sortedPlayers = getSortedHighScores()
        if (place <= 0 || place > sortedPlayers.size) return -1
        val uuid = sortedPlayers[place - 1]
        return Database.getHighScore(uuid)
    }

    private fun getPoints(place: Int): Int {
        val sortedPlayers = getSortedPoints()
        if (place <= 0 || place > sortedPlayers.size) return -1
        val uuid = sortedPlayers[place - 1]
        return Database.getPoints(uuid)
    }

    private fun getName(uuid: UUID): String {
        val player = Bukkit.getOfflinePlayer(uuid)
        return player.name ?: "Unknown"
    }

    private fun getSortedHighScores(): ObjectList<UUID> {
        val highScores = Database.highScores
        return highScores.entries.stream()
            .sorted(Comparator.comparingInt { it.value ?: 0 })
            .map { it.key }
            .collect(Collectors.toCollection { ObjectArrayList() })
    }

    private fun getSortedPoints(): ObjectList<UUID> {
        val points = Database.points
        return points.entries.stream()
            .sorted(Comparator.comparingInt { it.value ?: 0 })
            .map { it.key }
            .collect(Collectors.toCollection { ObjectArrayList() })
    }
}