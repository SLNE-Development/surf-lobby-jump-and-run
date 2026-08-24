package dev.slne.surf.parkour.core.client.model.geometry

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class BlockColumnTest {

    @Test
    fun `tells two columns apart whichever sign their coordinates carry`() {
        val columns = listOf(
            packColumn(0, 0),
            packColumn(1, 0),
            packColumn(0, 1),
            packColumn(-1, 0),
            packColumn(0, -1),
            packColumn(-1, -1),
            packColumn(Int.MIN_VALUE, Int.MAX_VALUE),
            packColumn(Int.MAX_VALUE, Int.MIN_VALUE)
        )

        assertEquals(columns.size, columns.toSet().size)
    }

    @Test
    fun `reads the column off the block a point lies in`() {
        assertEquals(packColumn(3, -8), ParkourVector(3.75, 64.0, -7.25).blockColumn)
        assertEquals(packColumn(3, -8), ParkourVector(3.0, 12.0, -8.0).blockColumn)
    }

    @Test
    fun `keeps points in neighbouring columns apart`() {
        assertNotEquals(
            ParkourVector(3.75, 64.0, -7.25).blockColumn,
            ParkourVector(4.0, 64.0, -7.25).blockColumn
        )
    }
}
