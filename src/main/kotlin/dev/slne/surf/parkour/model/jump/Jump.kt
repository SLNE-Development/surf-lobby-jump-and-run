package dev.slne.surf.parkour.model.jump

import org.bukkit.entity.Player
import org.bukkit.util.BoundingBox
import org.bukkit.util.Vector

data class Jump(
    val forward: Int,
    val lateral: Int,
    val vertical: Int
) {
    fun generate(previous: Vector, player: Player, area: BoundingBox): Vector {
        val yawRad = Math.toRadians(player.location.yaw.toDouble())

        val forwardVec = Vector(-kotlin.math.sin(yawRad), 0.0, kotlin.math.cos(yawRad)).normalize()
        val lateralVec = Vector(forwardVec.z, 0.0, -forwardVec.x).normalize()

        val offset = forwardVec.clone().multiply(forward.toDouble())
        offset.add(lateralVec.clone().multiply(lateral.toDouble()))

        offset.y = vertical.toDouble()

        var target = previous.clone().add(offset)

        if (!area.contains(target)) {
            target = area.clamp(target)
        }

        if (previous.distance(target) < 2.0) {
            val direction = (target.clone().subtract(previous)).normalize()
            target = previous.clone().add(direction.multiply(2.0))
        }

        return target
    }

    private fun BoundingBox.clamp(vector: Vector): Vector {
        return Vector(
            vector.x.coerceIn(this.minX, this.maxX),
            vector.y.coerceIn(this.minY, this.maxY),
            vector.z.coerceIn(this.minZ, this.maxZ)
        )
    }
}