package dev.slne.surf.parkour.core.client.menu

/**
 * The orders the parkour leaderboard can be read in.
 */
enum class ParkourLeaderboardSortType {
    HIGHSCORE,
    MOST_JUMPS,
    LEAST_JUMPS,
    MOST_TRIES,
    LEAST_TRIES;

    fun next() = entries[(ordinal + 1) % entries.size]
    fun previous() = entries[(ordinal - 1 + entries.size) % entries.size]
}
