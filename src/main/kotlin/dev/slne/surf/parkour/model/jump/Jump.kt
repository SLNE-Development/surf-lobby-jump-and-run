package dev.slne.surf.parkour.model.jump

import org.bukkit.entity.Player
import org.bukkit.util.BoundingBox
import org.bukkit.util.Vector

data class Jump(
    val forward: Int,
    val lateral: Int,
    val vertical: Int
) {
    fun generate(previous: Vector, current: Vector, player: Player, area: BoundingBox, otherPlayersBlocks: List<Vector> = emptyList()): Vector {
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

            target.x = target.x.coerceIn(area.minX, area.maxX)
            target.y = target.y.coerceIn(area.minY, area.maxY)
            target.z = target.z.coerceIn(area.minZ, area.maxZ)

            tries++
            
            val hasCollision = previous.distance(target) < 2.0 ||
                    (target.blockX == previous.blockX && target.blockZ == previous.blockZ) ||
                    (target.blockX == current.blockX && target.blockZ == current.blockZ) ||
                    otherPlayersBlocks.any { it.blockX == target.blockX && it.blockZ == target.blockZ }
            
            if (!hasCollision) {
                return target
            }
        } while (tries < 10)
        
        // If we couldn't find a valid position after 10 tries, return a fallback position
        // that's at least 2 blocks away from previous block to avoid immediate collision
        return previous.clone().add(forwardVec.clone().multiply(3.0)).apply {
            y += 0.0
            x = x.coerceIn(area.minX, area.maxX)
            y = y.coerceIn(area.minY, area.maxY)
            z = z.coerceIn(area.minZ, area.maxZ)
        }
    }
}