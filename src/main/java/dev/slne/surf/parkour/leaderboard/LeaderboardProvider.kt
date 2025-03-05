package dev.slne.surf.parkour.leaderboard

import dev.slne.surf.parkour.database.DatabaseProvider
import dev.slne.surf.parkour.player.PlayerData

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.selectAll

import java.util.UUID

object LeaderboardProvider {
    suspend fun getPointsPlace(place: Int): PlayerData {
        return withContext(Dispatchers.IO) {
            val uuid = DatabaseProvider.Users.selectAll()
                .orderBy(DatabaseProvider.Users.points, SortOrder.DESC)
                .limit(place)
                .map { UUID.fromString(it[DatabaseProvider.Users.uuid]) }
                .getOrNull(place - 1) ?: return@withContext PlayerData(UUID.randomUUID(), name = "Unknown")

            DatabaseProvider.getPlayerData(uuid)
        }
    }

    suspend fun getHighscorePlace(place: Int): PlayerData {
        return withContext(Dispatchers.IO) {
            val uuid = DatabaseProvider.Users.selectAll()
                .orderBy(DatabaseProvider.Users.highScore, SortOrder.DESC)
                .limit(place)
                .map { UUID.fromString(it[DatabaseProvider.Users.uuid]) }
                .getOrNull(place - 1) ?: return@withContext PlayerData(UUID.randomUUID(), name = "Unknown")

            DatabaseProvider.getPlayerData(uuid)
        }
    }
}