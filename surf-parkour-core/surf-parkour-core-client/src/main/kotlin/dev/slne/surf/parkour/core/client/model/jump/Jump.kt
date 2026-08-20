package dev.slne.surf.parkour.core.client.model.jump

import dev.slne.surf.api.core.util.intListOf
import dev.slne.surf.parkour.core.client.model.geometry.ParkourRegion
import dev.slne.surf.parkour.core.client.model.geometry.ParkourVector
import kotlin.math.cos
import kotlin.math.sin

/**
 * One jump of a parkour, described relative to the block it starts from.
 *
 * @property forward how many blocks the jump leads away from the player's facing direction
 * @property lateral how many blocks the jump leads to the side
 * @property vertical how many blocks the jump leads up or down
 */
data class Jump(
    val forward: Int,
    val lateral: Int,
    val vertical: Int
) {
    companion object {
        private const val FALLBACK_FORWARD_DISTANCE = 4.0
    }

    /**
     * Returns where this jump lands when taken from [previous] while looking at [yaw].
     *
     * The result stays inside [area] and avoids [current] as well as every block in
     * [otherPlayersBlocks], falling back to increasingly simple jumps while no free spot is found.
     */
    fun generate(
        previous: ParkourVector,
        current: ParkourVector,
        yaw: Float,
        area: ParkourRegion,
        otherPlayersBlocks: List<ParkourVector> = emptyList()
    ): ParkourVector {
        val yawRad = Math.toRadians(yaw.toDouble())
        val forwardVec = ParkourVector(-sin(yawRad), 0.0, cos(yawRad)).normalize()
        val lateralVec = ParkourVector(forwardVec.z, 0.0, -forwardVec.x)

        var target: ParkourVector
        var tries = 0

        do {
            val f = forward.coerceIn(2..4)
            val l = lateral.coerceIn(-2..2)
            val v = vertical.coerceIn(-1..1)

            target = previous + forwardVec * f.toDouble()
            target += lateralVec * l.toDouble()
            target = target.addY(v.toDouble())

            target = area.clamp(target)

            tries++

            if (!hasCollision(target, previous, current, otherPlayersBlocks)) {
                return target
            }
        } while (tries < 10)

        for (fallbackDistance in 3..6) {
            val fallback =
                area.clamp(previous + forwardVec * fallbackDistance.toDouble())

            if (!hasCollision(fallback, previous, current, otherPlayersBlocks)) {
                return fallback
            }
        }

        for (lateralOffset in intArrayOf(-2, 2, -1, 1)) {
            val lastResort = area.clamp(
                previous + forwardVec * FALLBACK_FORWARD_DISTANCE +
                        lateralVec * lateralOffset.toDouble()
            )

            if (!hasCollision(lastResort, previous, current, otherPlayersBlocks)) {
                return lastResort
            }
        }

        return area.clamp(previous + forwardVec * FALLBACK_FORWARD_DISTANCE)
    }

    private fun hasCollision(
        target: ParkourVector,
        previous: ParkourVector,
        current: ParkourVector,
        otherPlayersBlocks: List<ParkourVector>
    ): Boolean {
        return previous.distance(target) < 2.0 ||
                (target.blockX == previous.blockX && target.blockZ == previous.blockZ) ||
                (target.blockX == current.blockX && target.blockZ == current.blockZ) ||
                otherPlayersBlocks.any { it.blockX == target.blockX && it.blockZ == target.blockZ }
    }
}
