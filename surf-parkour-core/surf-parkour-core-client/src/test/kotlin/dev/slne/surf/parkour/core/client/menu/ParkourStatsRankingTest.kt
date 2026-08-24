package dev.slne.surf.parkour.core.client.menu

import dev.slne.surf.parkour.api.data.ParkourStats
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class ParkourStatsRankingTest {

    private val names = mutableMapOf<UUID, String>()

    private val alice = stats(name = "Alice", highscore = 10, totalJumps = 100, totalRuns = 3)
    private val bob = stats(name = "Bob", highscore = 30, totalJumps = 50, totalRuns = 9)
    private val carol = stats(name = "Carol", highscore = 20, totalJumps = 200, totalRuns = 1)

    private val all = listOf(alice, bob, carol)

    @Test
    fun `ranks the highest score first`() {
        val ranked = rank(ParkourLeaderboardSortType.HIGHSCORE)

        assertEquals(listOf("Bob", "Carol", "Alice"), ranked.map { names.getValue(it.stats.playerUuid) })
        assertEquals(listOf(1, 2, 3), ranked.map { it.rank })
    }

    @Test
    fun `ranks the fewest jumps first when asked for the least`() {
        val ranked = rank(ParkourLeaderboardSortType.LEAST_JUMPS)

        assertEquals(listOf("Bob", "Alice", "Carol"), ranked.map { names.getValue(it.stats.playerUuid) })
    }

    @Test
    fun `ranks the most tries first`() {
        val ranked = rank(ParkourLeaderboardSortType.MOST_TRIES)

        assertEquals(listOf("Bob", "Alice", "Carol"), ranked.map { names.getValue(it.stats.playerUuid) })
    }

    @Test
    fun `ranks the most jumps first`() {
        val ranked = rank(ParkourLeaderboardSortType.MOST_JUMPS)

        assertEquals(listOf("Carol", "Alice", "Bob"), ranked.map { names.getValue(it.stats.playerUuid) })
    }

    @Test
    fun `ranks the fewest tries first when asked for the least`() {
        val ranked = rank(ParkourLeaderboardSortType.LEAST_TRIES)

        assertEquals(listOf("Carol", "Alice", "Bob"), ranked.map { names.getValue(it.stats.playerUuid) })
    }

    @Test
    fun `keeps the place a filtered entry holds in the full leaderboard`() {
        val ranked = rank(ParkourLeaderboardSortType.HIGHSCORE, search = "alice")

        assertEquals(1, ranked.size)
        assertEquals(3, ranked.single().rank)
    }

    @Test
    fun `matches a search anywhere in the name, ignoring case`() {
        assertEquals(1, rank(ParkourLeaderboardSortType.HIGHSCORE, search = "RO").size)
    }

    @Test
    fun `keeps everyone when the search is blank`() {
        assertEquals(3, rank(ParkourLeaderboardSortType.HIGHSCORE, search = "   ").size)
    }

    private fun rank(sortType: ParkourLeaderboardSortType, search: String? = null) =
        rankParkourStats(all, sortType, search) { names.getValue(it.playerUuid) }

    private fun stats(
        name: String,
        highscore: Int,
        totalJumps: Int,
        totalRuns: Int
    ): ParkourStats {
        val uuid = UUID.randomUUID()
        names[uuid] = name

        return ParkourStats(
            playerUuid = uuid,
            totalRuns = totalRuns,
            totalJumps = totalJumps,
            highscore = highscore,
            averageTime = 0L
        )
    }
}
