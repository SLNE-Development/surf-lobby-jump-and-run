package dev.slne.surf.parkour.paper.util

import com.destroystokyo.paper.profile.ProfileProperty
import dev.slne.surf.parkour.core.common.service.playerTextureService
import dev.slne.surf.surfapi.bukkit.api.builder.buildItem
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.ResolvableProfile
import io.papermc.paper.datacomponent.item.TooltipDisplay
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.util.*

fun UUID.getPlayer() = Bukkit.getPlayer(this)

/**
 * Gets all blocks below the player's feet by checking their bounding box.
 * This is useful for detecting when a player is standing on the edge of a block.
 * 
 * The function checks slightly below the player's feet (minY - FOOT_CHECK_OFFSET) to ensure
 * we detect the blocks the player is actually standing on. It returns all blocks that overlap
 * with the player's bounding box, including corners, edges, and center blocks.
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
 * Helper function to get all unique blocks within a rectangular area at foot level.
 * This checks all blocks that overlap with the bounding box, not just the corners.
 */
private fun getBlocksAtCorners(
    minX: Double,
    maxX: Double,
    minZ: Double,
    maxZ: Double,
    y: Double
): MutableList<Vector> {
    val blocks = mutableSetOf<Vector>()

    // Get the block coordinates for the bounds
    val minBlockX = kotlin.math.floor(minX).toInt()
    val maxBlockX = kotlin.math.floor(maxX).toInt()
    val minBlockZ = kotlin.math.floor(minZ).toInt()
    val maxBlockZ = kotlin.math.floor(maxZ).toInt()
    val blockY = kotlin.math.floor(y).toInt()

    // Check all blocks within the bounding box range
    for (x in minBlockX..maxBlockX) {
        for (z in minBlockZ..maxBlockZ) {
            blocks.add(Vector(x.toDouble(), blockY.toDouble(), z.toDouble()))
        }
    }

    return blocks.toMutableList()
}

fun formatMillis(time: Long): String {
    val totalSeconds = time / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return when {
        hours > 0 -> "%02dh %02dm %02ds".format(hours, minutes, seconds)
        minutes > 0 -> "%02dm %02ds".format(minutes, seconds)
        else -> "%02ds".format(seconds)
    }
}

/**
 * Offset below player's feet for block detection.
 * A small offset ensures we check the block the player is standing on.
 */
private const val FOOT_CHECK_OFFSET = 0.1

@Suppress("UnstableApiUsage")
fun UUID.playerHead() = buildItem(Material.PLAYER_HEAD) {
    setData(
        DataComponentTypes.PROFILE, ResolvableProfile.resolvableProfile().addProperty(
            ProfileProperty("textures", playerTextureService.getTexture(this@playerHead).texture)
        ).build()
    )
    setData(
        DataComponentTypes.TOOLTIP_DISPLAY,
        TooltipDisplay.tooltipDisplay().addHiddenComponents(DataComponentTypes.PROFILE).build()
    )
}