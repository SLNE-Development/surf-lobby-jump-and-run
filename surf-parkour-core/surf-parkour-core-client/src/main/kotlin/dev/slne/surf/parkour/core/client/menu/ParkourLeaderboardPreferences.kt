package dev.slne.surf.parkour.core.client.menu

import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Remembers how every player last looked at the leaderboard, so reopening it keeps their view.
 */
object ParkourLeaderboardPreferences {
    private val sorts = ConcurrentHashMap<UUID, ParkourLeaderboardSortType>()
    private val searches = ConcurrentHashMap<UUID, String>()

    fun sortingByPlayer(playerUuid: UUID): ParkourLeaderboardSortType =
        sorts.getOrDefault(playerUuid, ParkourLeaderboardSortType.HIGHSCORE)

    fun searchByPlayer(playerUuid: UUID): String? =
        searches[playerUuid]

    fun setSorting(playerUuid: UUID, sortType: ParkourLeaderboardSortType) {
        sorts[playerUuid] = sortType
    }

    fun setSearch(playerUuid: UUID, filterValue: String?) {
        if (filterValue == null) {
            searches.remove(playerUuid)
        } else {
            searches[playerUuid] = filterValue
        }
    }

    /**
     * Drops what is remembered about the player identified by [playerUuid].
     */
    fun forget(playerUuid: UUID) {
        sorts.remove(playerUuid)
        searches.remove(playerUuid)
    }
}
