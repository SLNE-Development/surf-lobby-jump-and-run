package dev.slne.surf.parkour.core.client.menu

import dev.slne.surf.parkour.api.data.ParkourStats
import dev.slne.surf.parkour.core.client.service.ParkourRunsService
import dev.slne.surf.parkour.core.client.service.ParkourTexturesService
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import java.util.*
import java.util.Comparator.comparingInt

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
    val sortedGlobal = stats.sortedWith(sortType.comparator)

    if (search.isNullOrBlank()) {
        return sortedGlobal.mapIndexed { index, entry ->
            RankedParkourStats(entry, index + 1)
        }
    }

    val matches = ObjectArrayList<RankedParkourStats>()

    for (index in sortedGlobal.indices) {
        val entry = sortedGlobal[index]

        if (nameOf(entry).contains(search, ignoreCase = true)) {
            matches.add(RankedParkourStats(entry, index + 1))
        }
    }

    return matches
}

/**
 * The order this sorting reads the leaderboard in.
 */
private val ParkourLeaderboardSortType.comparator: Comparator<ParkourStats>
    get() = when (this) {
        ParkourLeaderboardSortType.HIGHSCORE -> HIGHSCORE_DESCENDING
        ParkourLeaderboardSortType.MOST_JUMPS -> TOTAL_JUMPS_DESCENDING
        ParkourLeaderboardSortType.LEAST_JUMPS -> TOTAL_JUMPS_ASCENDING
        ParkourLeaderboardSortType.MOST_TRIES -> TOTAL_RUNS_DESCENDING
        ParkourLeaderboardSortType.LEAST_TRIES -> TOTAL_RUNS_ASCENDING
    }

private val TOTAL_JUMPS_ASCENDING: Comparator<ParkourStats> = comparingInt { it.totalJumps }
private val TOTAL_JUMPS_DESCENDING: Comparator<ParkourStats> = TOTAL_JUMPS_ASCENDING.reversed()
private val TOTAL_RUNS_ASCENDING: Comparator<ParkourStats> = comparingInt { it.totalRuns }
private val TOTAL_RUNS_DESCENDING: Comparator<ParkourStats> = TOTAL_RUNS_ASCENDING.reversed()
private val HIGHSCORE_DESCENDING: Comparator<ParkourStats> =
    comparingInt<ParkourStats> { it.highscore }.reversed()
