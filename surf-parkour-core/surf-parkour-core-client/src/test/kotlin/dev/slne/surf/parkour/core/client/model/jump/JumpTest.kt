package dev.slne.surf.parkour.core.client.model.jump

import dev.slne.surf.parkour.core.client.model.geometry.ParkourRegion
import dev.slne.surf.parkour.core.client.model.geometry.ParkourVector
import dev.slne.surf.parkour.core.client.model.geometry.blockColumn
import it.unimi.dsi.fastutil.longs.LongOpenHashSet
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class JumpTest {

    private val area = ParkourRegion.of(ParkourVector(-64, 0, -64), ParkourVector(64, 64, 64))
    private val origin = ParkourVector(0, 10, 0)

    @Test
    fun `leads away from where the player looks`() {
        val target = Jump(3, 0, 0).generate(origin, origin, SOUTH, area)

        assertTrue(target.z > origin.z) { "expected a jump towards positive z, got $target" }
    }

    @Test
    fun `never leaves the area it is given`() {
        val narrow = ParkourRegion.of(ParkourVector(0, 10, 0), ParkourVector(1, 11, 1))

        val target = Jump(4, 2, 1).generate(origin, origin, SOUTH, narrow)

        assertTrue(target.x in narrow.minX..narrow.maxX)
        assertTrue(target.y in narrow.minY..narrow.maxY)
        assertTrue(target.z in narrow.minZ..narrow.maxZ)
    }

    @Test
    fun `keeps the vertical offset within one block`() {
        val target = Jump(3, 0, 5).generate(origin, origin, SOUTH, area)

        assertTrue(target.y - origin.y <= 1.0) { "expected at most one block up, got $target" }
    }

    @Test
    fun `avoids the column another player already stands in`() {
        val blocked = Jump(3, 0, 0).generate(origin, origin, SOUTH, area)
        val target = Jump(3, 0, 0).generate(origin, origin, SOUTH, area, listOf(blocked))

        assertNotEquals(blocked.blockX to blocked.blockZ, target.blockX to target.blockZ)
    }

    @Test
    fun `reads a list of blocked blocks the same way as their packed columns`() {
        val blocked = Jump(3, 0, 0).generate(origin, origin, SOUTH, area)

        val fromList = Jump(3, 0, 0).generate(origin, origin, SOUTH, area, listOf(blocked))
        val fromColumns = Jump(3, 0, 0)
            .generate(origin, origin, SOUTH, area, LongOpenHashSet.of(blocked.blockColumn))

        assertEquals(fromList, fromColumns)
    }

    @Test
    fun `never lands on the block it starts from`() {
        val target = Jump(0, 0, 0).generate(origin, origin, SOUTH, area)

        assertTrue(target.blockX != origin.blockX || target.blockZ != origin.blockZ)
    }

    private companion object {
        const val SOUTH = 0f
    }
}
