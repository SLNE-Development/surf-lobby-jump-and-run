package dev.slne.surf.parkour.`object`.jump

import org.bukkit.entity.Player
import org.bukkit.util.BoundingBox
import org.bukkit.util.Vector

data class Jump(
    private val forward: Int,
    private val lateral: Int,
    private val vertical: Int
) {
    fun generate(previous: Vector, player: Player, area: BoundingBox): Vector {
        val baseLocation = previous.clone()

        val yawRad = Math.toRadians(player.location.yaw.toDouble())
        val forwardVec =
            Vector(-kotlin.math.sin(yawRad), 0.0, kotlin.math.cos(yawRad))
                .normalize()
        val lateralVec = Vector(forwardVec.z, 0.0, -forwardVec.x)

        val offset = forwardVec.multiply(forward).add(lateralVec.multiply(lateral))
        offset.y = vertical.toDouble()

        var finalLocation = baseLocation.clone().add(offset)

        if (!area.contains(finalLocation)) {
            var found = false
            var attempts = 1

            while (attempts <= 5 && !found) {
                val left = baseLocation.clone().add(lateralVec.multiply(-attempts))
                val right = baseLocation.clone().add(lateralVec.multiply(attempts))

                if (area.contains(left)) {
                    finalLocation = left
                    found = true
                } else if (area.contains(right)) {
                    finalLocation = right
                    found = true
                }

                attempts++
            }

            if (!found) {
                finalLocation = baseLocation.clone().subtract(forwardVec)
            }
        }

        return finalLocation.add(Vector(1.0, 0.0, 0.0))
    }
}