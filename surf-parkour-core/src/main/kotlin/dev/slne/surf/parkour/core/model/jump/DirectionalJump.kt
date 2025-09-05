package dev.slne.surf.parkour.core.model.jump

import dev.slne.surf.parkour.api.model.Jump
import dev.slne.surf.parkour.api.model.parkour.ParkourArea
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import kotlin.math.cos
import kotlin.math.sin

abstract class DirectionalJump(
    private val forward: Int,
    private val lateral: Int,
    private val vertical: Int
) : Jump {
    override fun generate(previous: Block?, player: Player, area: ParkourArea): Block {
        val baseLocation = previous?.location ?: player.location.clone().add(0.0, -1.0, 0.0)

        val yawRad = Math.toRadians(player.location.yaw.toDouble())
        val forwardVec = Vector(-sin(yawRad), 0.0, cos(yawRad)).normalize()
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
                finalLocation = baseLocation.clone().subtract(forwardVec.multiply(3))
            }
        }

        return finalLocation.block
    }
}

