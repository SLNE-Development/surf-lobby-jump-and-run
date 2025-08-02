package dev.slne.surf.parkour.core.model.jump

import dev.slne.surf.parkour.api.model.Jump
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.util.Vector

abstract class DirectionalJump(
    private val forward: Int,
    private val lateral: Int
) : Jump {
    override fun generate(previous: Block?, player: Player): Block {
        val baseLocation = previous?.location ?: player.location.clone().add(0.0, -1.0, 0.0)

        val yaw = ((player.location.yaw % 360) + 360) % 360

        val offset = when (yaw) {
            in 315.0..360.0, in 0.0..45.0 -> Vector(-lateral, 0, -forward)
            in 45.0..135.0 -> Vector(forward, 0, -lateral)
            in 135.0..225.0 -> Vector(lateral, 0, forward)
            else -> Vector(-forward, 0, lateral)
        }

        val final = baseLocation.clone().add(offset)
        return final.block
    }
}