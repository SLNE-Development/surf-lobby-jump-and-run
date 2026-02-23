package dev.slne.surf.parkour.paper.menu.sort

import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import java.util.*

enum class ParkourLeaderboardSortType {
    HIGHSCORE,
    MOST_JUMPS,
    LEAST_JUMPS,
    MOST_TRIES,
    LEAST_TRIES;

    fun next() = entries[(ordinal + 1) % entries.size]
    fun previous() = entries[(ordinal - 1 + entries.size) % entries.size]

    companion object {
        private val sorts = mutableObject2ObjectMapOf<UUID, ParkourLeaderboardSortType>()
        private val searches = mutableObject2ObjectMapOf<UUID, String>()

        fun sortingByPlayer(playerUuid: UUID): ParkourLeaderboardSortType =
            sorts.getOrDefault(playerUuid, HIGHSCORE)

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
    }
}