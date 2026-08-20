package dev.slne.surf.parkour.core.client.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TimeFormatTest {

    @Test
    fun `writes a duration below a second as zero seconds`() {
        assertEquals("0s", 999L.formattedDuration)
    }

    @Test
    fun `leaves out units that contribute nothing`() {
        assertEquals("1h 5s", (3_600_000L + 5_000L).formattedDuration)
    }

    @Test
    fun `writes hours, minutes and seconds it covers`() {
        assertEquals("2h 3m 4s", (2 * 3_600_000L + 3 * 60_000L + 4_000L).formattedDuration)
    }

    @Test
    fun `pads every unit to two digits`() {
        assertEquals("01h 02m 03s", formatMillis(3_600_000L + 2 * 60_000L + 3_000L))
    }

    @Test
    fun `starts at the largest unit that contributes`() {
        assertEquals("02m 03s", formatMillis(2 * 60_000L + 3_000L))
        assertEquals("03s", formatMillis(3_000L))
        assertEquals("00s", formatMillis(0L))
    }
}
