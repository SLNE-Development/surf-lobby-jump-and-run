package dev.slne.surf.parkour.core.client.menu

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.util.*

class ParkourLeaderboardPreferencesTest {

    @Test
    fun `keeps what a player last looked at`() {
        val player = UUID.randomUUID()

        ParkourLeaderboardPreferences.setSorting(player, ParkourLeaderboardSortType.LEAST_TRIES)
        ParkourLeaderboardPreferences.setSearch(player, "red")

        assertEquals(
            ParkourLeaderboardSortType.LEAST_TRIES,
            ParkourLeaderboardPreferences.sortingByPlayer(player)
        )
        assertEquals("red", ParkourLeaderboardPreferences.searchByPlayer(player))
    }

    @Test
    fun `forgets a player once they leave`() {
        val player = UUID.randomUUID()

        ParkourLeaderboardPreferences.setSorting(player, ParkourLeaderboardSortType.MOST_JUMPS)
        ParkourLeaderboardPreferences.setSearch(player, "red")

        ParkourLeaderboardPreferences.forget(player)

        assertEquals(
            ParkourLeaderboardSortType.HIGHSCORE,
            ParkourLeaderboardPreferences.sortingByPlayer(player)
        )
        assertNull(ParkourLeaderboardPreferences.searchByPlayer(player))
    }

    @Test
    fun `leaves other players alone when one is forgotten`() {
        val leaving = UUID.randomUUID()
        val staying = UUID.randomUUID()

        ParkourLeaderboardPreferences.setSorting(leaving, ParkourLeaderboardSortType.MOST_TRIES)
        ParkourLeaderboardPreferences.setSorting(staying, ParkourLeaderboardSortType.LEAST_JUMPS)

        ParkourLeaderboardPreferences.forget(leaving)

        assertEquals(
            ParkourLeaderboardSortType.LEAST_JUMPS,
            ParkourLeaderboardPreferences.sortingByPlayer(staying)
        )
    }
}
