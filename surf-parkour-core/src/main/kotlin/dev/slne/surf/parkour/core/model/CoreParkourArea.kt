package dev.slne.surf.parkour.core.model

import dev.slne.surf.parkour.api.model.parkour.ParkourArea
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import kotlin.math.max
import kotlin.math.min

data class CoreParkourArea(
    override val world: World,
    override val firstLocation: Location,
    override val secondLocation: Location
) : ParkourArea {
    override fun contains(location: Location): Boolean {
        if (location.world != world) return false

        val minX = min(firstLocation.blockX, secondLocation.blockX)
        val maxX = max(firstLocation.blockX, secondLocation.blockX)
        val minY = min(firstLocation.blockY, secondLocation.blockY)
        val maxY = max(firstLocation.blockY, secondLocation.blockY)
        val minZ = min(firstLocation.blockZ, secondLocation.blockZ)
        val maxZ = max(firstLocation.blockZ, secondLocation.blockZ)

        return location.blockX in minX..maxX &&
                location.blockY in minY..maxY &&
                location.blockZ in minZ..maxZ
    }

    override fun contains(block: Block) = contains(block.location)
}
