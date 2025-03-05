package dev.slne.surf.parkour.papi

import com.github.shynixn.mccoroutine.bukkit.launch
import dev.slne.surf.parkour.database.DatabaseProvider
import dev.slne.surf.parkour.instance
import dev.slne.surf.parkour.player.PlayerData
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withContext
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.OfflinePlayer
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.selectAll
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
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

    override fun getIdentifier(): String = IDENTIFIER
    override fun getAuthor(): String = AUTHOR
    override fun getVersion(): String = VERSION

    override fun onRequest(player: OfflinePlayer, params: String): String? {
        return "Coming Soon™️"

//        val parts = params.split("_")
//        if (parts.size < 3) return null
//
//        val category = parts[0]
//        val rank = parts[1].toIntOrNull() ?: return null
//        val suffix = parts[2]
//
//        val deferred = CompletableDeferred<String>()
//
//        instance.launch {
//            val playerData = getRankedPlayer(category, rank)
//            val result = when (suffix) {
//                SUFFIX_NAME -> playerData.name ?: "/"
//                SUFFIX_VALUE -> playerData.let {
//                    when (category) {
//                        CATEGORY_HIGHSCORE -> it.highScore.toString()
//                        CATEGORY_POINTS -> it.points.toString()
//                        else -> "/"
//                    }
//                }
//                else -> "/"
//            }
//
//            deferred.complete(result)
//        }
//
//        return deferred.getCompleted()
    }

    private suspend fun getRankedPlayer(category: String, rank: Int): PlayerData {
        return withContext(Dispatchers.IO) {
            val stats = DatabaseProvider.Users.selectAll()
                .orderBy(
                    when (category) {
                        CATEGORY_HIGHSCORE -> DatabaseProvider.Users.highScore to SortOrder.DESC
                        CATEGORY_POINTS -> DatabaseProvider.Users.points to SortOrder.DESC
                        else -> DatabaseProvider.Users.highScore to SortOrder.DESC
                    }
                )
                .limit(rank)
                .map { PlayerData(
                    UUID.fromString(it[DatabaseProvider.Users.uuid]),
                    it[DatabaseProvider.Users.name],
                    it[DatabaseProvider.Users.highScore],
                    it[DatabaseProvider.Users.points],
                    it[DatabaseProvider.Users.trys],
                    it[DatabaseProvider.Users.likesSound]
                )
                }

            return@withContext stats.getOrNull(rank - 1) ?: PlayerData(UUID.randomUUID())
        }
    }
}
