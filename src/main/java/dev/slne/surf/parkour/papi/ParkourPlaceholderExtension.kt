import com.github.benmanes.caffeine.cache.Caffeine
import dev.slne.surf.parkour.database.DatabaseProvider
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unimi.dsi.fastutil.objects.ObjectList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import java.util.concurrent.TimeUnit

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

    private val highscoreCache = Caffeine.newBuilder()
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .build<Int, Pair<String, Int>>()

    private val pointsCache = Caffeine.newBuilder()
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .build<Int, Pair<String, Int>>()

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
            CATEGORY_HIGHSCORE -> getCachedValue(highscoreCache, place, ::getSortedHighScores, ::getHighScore, suffix)
            CATEGORY_POINTS -> getCachedValue(pointsCache, place, ::getSortedPoints, ::getPoints, suffix)
            else -> null
        }
    }

    private fun getCachedValue(
        cache: com.github.benmanes.caffeine.cache.Cache<Int, Pair<String, Int>>,
        place: Int,
        sortedFetcher: suspend (Int) -> ObjectList<UUID>,
        valueFetcher: suspend (Int) -> Int,
        suffix: String
    ): String {
        val cached = cache.getIfPresent(place)
        if (cached != null) {
            return when (suffix) {
                SUFFIX_NAME -> cached.first
                SUFFIX_VALUE -> cached.second.toString()
                else -> "/"
            }
        }

        // Asynchron laden, um Main-Thread nicht zu blockieren
        GlobalScope.launch {
            val sortedPlayers = sortedFetcher(10)
            val uuid = sortedPlayers.getOrNull(place - 1)
            val name = if (uuid != null) getName(uuid) else "/"
            val value = valueFetcher(place)

            cache.put(place, name to value)
        }

        return "Lade..."
    }

    private fun getName(uuid: UUID): String {
        val player = Bukkit.getOfflinePlayer(uuid)
        return player.name ?: "Unknown"
    }

    private suspend fun getSortedHighScores(limit: Int): ObjectList<UUID> {
        return withContext(Dispatchers.IO) {
            transaction {
                DatabaseProvider.Users.selectAll()
                    .orderBy(DatabaseProvider.Users.highScore, SortOrder.DESC)
                    .limit(limit)
                    .map { UUID.fromString(it[DatabaseProvider.Users.uuid]) }.toObjectList()
            }
        }
    }

    private suspend fun getSortedPoints(limit: Int): ObjectList<UUID> {
        return withContext(Dispatchers.IO) {
            transaction {
                DatabaseProvider.Users.selectAll()
                    .orderBy(DatabaseProvider.Users.points, SortOrder.DESC)
                    .limit(limit)
                    .map { UUID.fromString(it[DatabaseProvider.Users.uuid]) }.toObjectList()
            }
        }
    }

    private suspend fun getHighScore(place: Int): Int {
        return withContext(Dispatchers.IO) {
            transaction {
                DatabaseProvider.Users.selectAll()
                    .orderBy(DatabaseProvider.Users.highScore, SortOrder.DESC)
                    .map { it[DatabaseProvider.Users.highScore] }
                    .getOrNull(place - 1) ?: 0
            }
        }
    }

    private suspend fun getPoints(place: Int): Int {
        return withContext(Dispatchers.IO) {
            transaction {
                DatabaseProvider.Users.selectAll()
                    .orderBy(DatabaseProvider.Users.points, SortOrder.DESC)
                    .map { it[DatabaseProvider.Users.points] }
                    .getOrNull(place - 1) ?: 0
            }
        }
    }

    private fun <T> List<T>.toObjectList(): ObjectList<T> {
        return ObjectArrayList(this)
    }
}
