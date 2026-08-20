package dev.slne.surf.parkour.core.client.service

import dev.slne.surf.parkour.core.client.model.geometry.ParkourVector
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ParkourMoveServiceTest {

    private val blocks = Triple(
        ParkourVector(0, 10, 0),
        ParkourVector(3, 10, 0),
        ParkourVector(6, 11, 0)
    )

    @Test
    fun `reports a fall once the player is below every block`() {
        assertTrue(ParkourMoveService.hasFallenBelow(blocks, 9.0))
    }

    @Test
    fun `reports no fall while the player is level with the lowest block`() {
        assertFalse(ParkourMoveService.hasFallenBelow(blocks, 10.0))
    }

    @Test
    fun `counts standing on the center of the target block`() {
        val target = ParkourVector(3, 10, 7)

        assertTrue(ParkourMoveService.isOnTargetBlock(3.5, 10.0, 7.5, target))
    }

    @Test
    fun `counts standing on the very edge of the target block`() {
        val target = ParkourVector(3, 10, 7)

        assertTrue(ParkourMoveService.isOnTargetBlock(2.5, 10.0, 6.5, target))
        assertTrue(ParkourMoveService.isOnTargetBlock(4.5, 10.0, 8.5, target))
    }

    @Test
    fun `does not count standing beside the target block`() {
        val target = ParkourVector(3, 10, 7)

        assertFalse(ParkourMoveService.isOnTargetBlock(6.0, 10.0, 7.5, target))
    }

    @Test
    fun `does not count standing at another height`() {
        val target = ParkourVector(3, 10, 7)

        assertFalse(ParkourMoveService.isOnTargetBlock(3.5, 12.0, 7.5, target))
    }
}
