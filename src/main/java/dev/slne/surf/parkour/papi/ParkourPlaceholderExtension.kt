package dev.slne.surf.parkour.papi

//import com.github.shynixn.mccoroutine.bukkit.launch
//import dev.slne.surf.parkour.SurfParkour
//import dev.slne.surf.parkour.database.DatabaseProvider
//import dev.slne.surf.parkour.instance
//import it.unimi.dsi.fastutil.objects.ObjectArrayList
//import it.unimi.dsi.fastutil.objects.ObjectList
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//import me.clip.placeholderapi.expansion.PlaceholderExpansion
//import org.bukkit.Bukkit
//import org.bukkit.OfflinePlayer
//import org.jetbrains.exposed.sql.SortOrder
//import org.jetbrains.exposed.sql.selectAll
//import org.jetbrains.exposed.sql.transactions.transaction
//import java.util.*
//import kotlin.collections.List
//
//class ParkourPlaceholderExtension : PlaceholderExpansion() {
//    companion object {
//        private const val IDENTIFIER = "surf-parkour"
//        private const val AUTHOR = "SLNE Development, TheBjoRedCraft"
//        private const val VERSION = "1.0.0"
//        private const val CATEGORY_HIGHSCORE = "highscore"
//        private const val CATEGORY_POINTS = "points"
//        private const val SUFFIX_NAME = "name"
//        private const val SUFFIX_VALUE = "value"
//    }
//
//    /*
//    Um die Platzhalter abzufragen, kannst du die folgenden Beispiele verwenden:
//    Um den Namen des Spielers mit dem höchsten Highscore abzufragen:
//    %surf-parkour_highscore_1_name%
//
//    Um den Wert des höchsten Highscores abzufragen:
//    %surf-parkour_highscore_1_value%
//
//    Um den Namen des Spielers mit den meisten Punkten abzufragen:
//    %surf-parkour_points_1_name%
//
//    Um den Wert der meisten Punkte abzufragen:
//    %surf-parkour_points_1_value%
//     */
//
//    override fun getIdentifier(): String = IDENTIFIER
//    override fun getAuthor(): String = AUTHOR
//    override fun getVersion(): String = VERSION
//
//    override fun onRequest(player: OfflinePlayer, params: String): String? {
//        val parts = params.split("_")
//        if (parts.size < 3) return null
//
//        val category = parts[0]
//        val place = parts[1].toIntOrNull() ?: return null
//        val suffix = parts[2]
//
//        var result: String? = null
//        val job = instance.launch {
//            result = when (category) {
//                CATEGORY_HIGHSCORE -> handleRequestAsync(place, suffix, ::getHighScore, ::getSortedHighScores)
//                CATEGORY_POINTS -> handleRequestAsync(place, suffix, ::getPoints, ::getSortedPoints)
//                else -> null
//            }
//        }
//        job.invokeOnCompletion { /* Handle completion if needed */ }
//        return result
//    }
//
//    private fun handleRequestAsync(
//        place: Int,
//        suffix: String,
//        valueProvider: suspend (Int) -> Int,
//        sortedPlayersProvider: suspend () -> ObjectList<UUID>
//    ) = instance.launch {
//        when (suffix) {
//            SUFFIX_NAME -> getName(place, sortedPlayersProvider())
//            SUFFIX_VALUE -> valueProvider(place).toString()
//        }
//    }
//
//    private fun getName(place: Int, sortedPlayers: ObjectList<UUID>): String {
//        if (place <= 0 || place > sortedPlayers.size) return "/"
//        return getName(sortedPlayers[place - 1])
//    }
//
//    private fun getName(uuid: UUID): String {
//        val player = Bukkit.getOfflinePlayer(uuid)
//        return player.name ?: "Unknown"
//    }
//
//    private suspend fun getSortedHighScores(): ObjectList<UUID> {
//        return withContext(Dispatchers.IO) {
//            transaction {
//                DatabaseProvider.Users.selectAll()
//                    .orderBy(DatabaseProvider.Users.highScore, SortOrder.DESC)
//                    .map { it[DatabaseProvider.Users.uuid] }.toObjectList()
//            }
//        }
//    }
//
//    private suspend fun getSortedPoints(): ObjectList<UUID> {
//        return withContext(Dispatchers.IO) {
//            transaction {
//                DatabaseProvider.Users.selectAll()
//                    .orderBy(DatabaseProvider.Users.points, SortOrder.DESC)
//                    .map { it[DatabaseProvider.Users.uuid] }.toObjectList()
//            }
//        }
//    }
//
//    private suspend fun getHighScore(place: Int): Int {
//        return withContext(Dispatchers.IO) {
//            transaction {
//                DatabaseProvider.Users.selectAll()
//                    .orderBy(DatabaseProvider.Users.highScore, SortOrder.DESC)
//                    .map { it[DatabaseProvider.Users.highScore] }
//                    .getOrNull(place - 1) ?: 0
//            }
//        }
//    }
//
//    private suspend fun getPoints(place: Int): Int {
//        return withContext(Dispatchers.IO) {
//            transaction {
//                DatabaseProvider.Users.selectAll()
//                    .orderBy(DatabaseProvider.Users.points, SortOrder.DESC)
//                    .map { it[DatabaseProvider.Users.points] }
//                    .getOrNull(place - 1) ?: 0
//            }
//        }
//    }
//
//    private fun <T> List<T>.toObjectList(): ObjectList<T> {
//        return ObjectArrayList(this)
//    }
//}