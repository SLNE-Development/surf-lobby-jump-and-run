package dev.slne.surf.parkour.core.client.menu

import dev.slne.surf.parkour.api.data.ParkourStats
import dev.slne.surf.parkour.core.client.service.ParkourRunsService
import dev.slne.surf.parkour.core.client.service.ParkourTexturesService
import java.util.*

/**
 * A player's stats together with the place they take in the leaderboard.
 */
data class RankedParkourStats(
    val stats: ParkourStats,
    val rank: Int
)

/**
 * The name the stats' player was last seen under.
 */
val ParkourStats.playerName: String
    get() = ParkourTexturesService.getTexture(playerUuid).playerName

/**
 * The label the leaderboard shows for this sorting.
 */
val ParkourLeaderboardSortType.label: String
    get() = when (this) {
        ParkourLeaderboardSortType.HIGHSCORE -> "Highscore"
        ParkourLeaderboardSortType.MOST_JUMPS -> "Meiste Gesamtsprünge"
        ParkourLeaderboardSortType.LEAST_JUMPS -> "Wenigste Gesamtsprünge"
        ParkourLeaderboardSortType.MOST_TRIES -> "Meiste Versuche"
        ParkourLeaderboardSortType.LEAST_TRIES -> "Wenigste Versuche"
    }

/**
 * Ranks every player surf-parkour knows about by [sortType] and keeps only those whose name
 * contains [search].
 */
fun parkourStatsSortedAndFiltered(
    sortType: ParkourLeaderboardSortType,
    search: String?
): List<RankedParkourStats> =
    rankParkourStats(ParkourRunsService.stats, sortType, search) { it.playerName }

/**
 * Ranks every player the way [playerUuid] last looked at the leaderboard.
 */
fun parkourStatsFor(playerUuid: UUID): List<RankedParkourStats> =
    parkourStatsSortedAndFiltered(
        ParkourLeaderboardPreferences.sortingByPlayer(playerUuid),
        ParkourLeaderboardPreferences.searchByPlayer(playerUuid)
    )

/**
 * Ranks [stats] by [sortType], then keeps only those whose name - as [nameOf] reports it -
 * contains [search], ignoring case.
 *
 * Ranks are handed out before filtering, so every entry keeps the place it holds in the full
 * leaderboard.
 */
fun rankParkourStats(
    stats: List<ParkourStats>,
    sortType: ParkourLeaderboardSortType,
    search: String?,
    nameOf: (ParkourStats) -> String
): List<RankedParkourStats> {
    val sortedGlobal = when (sortType) {
        ParkourLeaderboardSortType.HIGHSCORE -> stats.sortedByDescending { it.highscore }
        ParkourLeaderboardSortType.MOST_JUMPS -> stats.sortedByDescending { it.totalJumps }
        ParkourLeaderboardSortType.LEAST_JUMPS -> stats.sortedBy { it.totalJumps }
        ParkourLeaderboardSortType.MOST_TRIES -> stats.sortedByDescending { it.totalRuns }
        ParkourLeaderboardSortType.LEAST_TRIES -> stats.sortedBy { it.totalRuns }
    }

    val rankedList = sortedGlobal.mapIndexed { index, entry ->
        RankedParkourStats(entry, index + 1)
    }

    return if (search.isNullOrBlank()) {
        rankedList
    } else {
        val query = search.lowercase()
        rankedList.filter {
            nameOf(it.stats).lowercase().contains(query)
        }
    }
}
