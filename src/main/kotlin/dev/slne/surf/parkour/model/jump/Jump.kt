package dev.slne.surf.parkour.model.jump

import org.bukkit.entity.Player
import org.bukkit.util.BoundingBox
import org.bukkit.util.Vector

data class Jump(
    val forward: Int,
    val lateral: Int,
    val vertical: Int
) {
    companion object {
        private const val FALLBACK_FORWARD_DISTANCE = 4.0
    }

    fun generate(
        previous: Vector,
        current: Vector,
        player: Player,
        area: BoundingBox,
        otherPlayersBlocks: List<Vector> = emptyList()
    ): Vector {
        val yawRad = Math.toRadians(player.location.yaw.toDouble())
        val forwardVec = Vector(-kotlin.math.sin(yawRad), 0.0, kotlin.math.cos(yawRad)).normalize()
        val lateralVec = Vector(forwardVec.z, 0.0, -forwardVec.x)

        var target: Vector
        var tries = 0

        do {
            val f = forward.coerceIn(2..4)
            val l = lateral.coerceIn(-2..2)
            val v = vertical.coerceIn(-1..1)

            target = previous.clone().add(forwardVec.clone().multiply(f.toDouble()))
            target.add(lateralVec.clone().multiply(l.toDouble()))
            target.y += v

            clampToBounds(target, area)

            tries++

            if (!hasCollision(target, previous, current, otherPlayersBlocks)) {
                return target
            }
        } while (tries < 10)

        for (fallbackDistance in 3..6) {
            val fallback =
                previous.clone().add(forwardVec.clone().multiply(fallbackDistance.toDouble()))
            clampToBounds(fallback, area)

            if (!hasCollision(fallback, previous, current, otherPlayersBlocks)) {
                return fallback
            }
        }

        for (lateralOffset in listOf(-2, 2, -1, 1)) {
            val lastResort = previous.clone()
                .add(forwardVec.clone().multiply(FALLBACK_FORWARD_DISTANCE))
                .add(lateralVec.clone().multiply(lateralOffset.toDouble()))
            clampToBounds(lastResort, area)

            if (!hasCollision(lastResort, previous, current, otherPlayersBlocks)) {
                return lastResort
            }
        }

        val absoluteFallback =
            previous.clone().add(forwardVec.clone().multiply(FALLBACK_FORWARD_DISTANCE))
        clampToBounds(absoluteFallback, area)
        return absoluteFallback
    }

    private fun clampToBounds(vector: Vector, area: BoundingBox) {
        vector.x = vector.x.coerceIn(area.minX, area.maxX)
        vector.y = vector.y.coerceIn(area.minY, area.maxY)
        vector.z = vector.z.coerceIn(area.minZ, area.maxZ)
    }

    private fun hasCollision(
        target: Vector,
        previous: Vector,
        current: Vector,
        otherPlayersBlocks: List<Vector>
    ): Boolean {
        return previous.distance(target) < 2.0 ||
                (target.blockX == previous.blockX && target.blockZ == previous.blockZ) ||
                (target.blockX == current.blockX && target.blockZ == current.blockZ) ||
                otherPlayersBlocks.any { it.blockX == target.blockX && it.blockZ == target.blockZ }
    }
}