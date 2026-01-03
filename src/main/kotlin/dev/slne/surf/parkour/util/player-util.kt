package dev.slne.surf.parkour.util

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.util.*

fun UUID.getPlayer() = Bukkit.getPlayer(this)

/**
 * Gets all blocks below the player's feet by checking the 4 corners of their bounding box.
 * This is useful for detecting when a player is standing on the edge of a block.
 */
fun Player.getBlocksBelowFeet(): List<Vector> {
    val boundingBox = this.boundingBox
    
    // Check the 4 corners of the player's bounding box at foot level
    val minX = boundingBox.minX
    val maxX = boundingBox.maxX
    val minZ = boundingBox.minZ
    val maxZ = boundingBox.maxZ
    val footY = boundingBox.minY - FOOT_CHECK_OFFSET
    
    val blocks = mutableSetOf<Vector>()
    
    // Check all 4 corners
    blocks.add(Vector(minX, footY, minZ).toBlockLocation())
    blocks.add(Vector(maxX, footY, minZ).toBlockLocation())
    blocks.add(Vector(minX, footY, maxZ).toBlockLocation())
    blocks.add(Vector(maxX, footY, maxZ).toBlockLocation())
    
    return blocks.toList()
}

private fun Vector.toBlockLocation(): Vector {
    return Vector(blockX.toDouble(), blockY.toDouble(), blockZ.toDouble())
}

/**
 * Offset below player's feet for block detection.
 * A small offset ensures we check the block the player is standing on.
 */
private const val FOOT_CHECK_OFFSET = 0.1