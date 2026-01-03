package dev.slne.surf.parkour.util

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.util.*

fun UUID.getPlayer() = Bukkit.getPlayer(this)

/**
 * Gets all blocks below the player's feet by checking the 4 corners of their bounding box.
 * This is useful for detecting when a player is standing on the edge of a block.
 * 
 * The function checks slightly below the player's feet (minY - FOOT_CHECK_OFFSET) to ensure
 * we detect the blocks the player is actually standing on.
 */
fun Player.getBlocksBelowFeet(): List<Vector> {
    val boundingBox = this.boundingBox
    
    // Check the 4 corners of the player's bounding box at foot level
    val minX = boundingBox.minX
    val maxX = boundingBox.maxX
    val minZ = boundingBox.minZ
    val maxZ = boundingBox.maxZ
    val footY = boundingBox.minY - FOOT_CHECK_OFFSET
    
    return getBlocksAtCorners(minX, maxX, minZ, maxZ, footY)
}

/**
 * Gets all blocks that could be below a specific location, accounting for being on block edges.
 * This checks the 4 corners of a player-sized bounding box centered at the given location.
 * 
 * @param location The location to check below
 * @return List of block vectors that could be below the given location
 */
fun getBlocksBelowLocation(location: Location): List<Vector> {
    val centerX = location.x
    val centerZ = location.z
    val footY = location.y - FOOT_CHECK_OFFSET
    
    // Calculate corners of a player-sized bounding box
    val minX = centerX - PLAYER_HALF_WIDTH
    val maxX = centerX + PLAYER_HALF_WIDTH
    val minZ = centerZ - PLAYER_HALF_WIDTH
    val maxZ = centerZ + PLAYER_HALF_WIDTH
    
    return getBlocksAtCorners(minX, maxX, minZ, maxZ, footY)
}

/**
 * Helper function to get unique blocks at the 4 corners of a rectangular area.
 */
private fun getBlocksAtCorners(minX: Double, maxX: Double, minZ: Double, maxZ: Double, y: Double): List<Vector> {
    val blocks = mutableSetOf<Vector>()
    
    blocks.add(Vector(minX, y, minZ).toBlockLocation())
    blocks.add(Vector(maxX, y, minZ).toBlockLocation())
    blocks.add(Vector(minX, y, maxZ).toBlockLocation())
    blocks.add(Vector(maxX, y, maxZ).toBlockLocation())
    
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

/**
 * Half-width of a player's bounding box.
 * Player bounding box is 0.6 blocks wide total (0.3 in each direction from center).
 */
private const val PLAYER_HALF_WIDTH = 0.3