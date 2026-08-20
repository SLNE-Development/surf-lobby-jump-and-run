package dev.slne.surf.parkour.core.client.model.geometry

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RotationTest {

    @Test
    fun `looks south for a target on the positive z axis`() {
        val (yaw, pitch) = calcRotation(ParkourVector(0, 0, 0), ParkourVector(0, 0, 5))

        assertEquals(0f, yaw, TOLERANCE)
        assertEquals(0f, pitch, TOLERANCE)
    }

    @Test
    fun `looks west for a target on the negative x axis`() {
        val (yaw, _) = calcRotation(ParkourVector(0, 0, 0), ParkourVector(-5, 0, 0))

        assertEquals(90f, yaw, TOLERANCE)
    }

    @Test
    fun `looks up for a target above`() {
        val (_, pitch) = calcRotation(ParkourVector(0, 0, 0), ParkourVector(0, 5, 5))

        assertEquals(-45f, pitch, TOLERANCE)
    }

    private companion object {
        const val TOLERANCE = 0.001f
    }
}
