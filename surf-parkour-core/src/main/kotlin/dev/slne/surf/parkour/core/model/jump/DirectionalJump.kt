package dev.slne.surf.parkour.core.model.jump

import dev.slne.surf.parkour.api.model.Jump
import dev.slne.surf.parkour.core.util.asSimpleString
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
    override fun generate(previous: Block?, player: Player): Block {
        val baseLocation = previous?.location ?: player.location.clone().add(0.0, -1.0, 0.0)

        println(
            "Generating jump from ${baseLocation.block.location.asSimpleString()} " +
                    "with forward $forward, lateral $lateral, vertical $vertical"
        )

        val yawRad = Math.toRadians(player.location.yaw.toDouble())
        val forwardVec = Vector(-sin(yawRad), 0.0, cos(yawRad)).normalize()
        val lateralVec = Vector(forwardVec.z, 0.0, -forwardVec.x)

        val offset = forwardVec.multiply(forward).add(lateralVec.multiply(lateral))

        offset.y = vertical.toDouble()

        val final = baseLocation.clone().add(offset)
        return final.block
    }
}
