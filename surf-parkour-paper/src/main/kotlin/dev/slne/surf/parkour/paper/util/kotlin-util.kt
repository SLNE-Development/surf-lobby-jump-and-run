package dev.slne.surf.parkour.paper.util

import com.github.retrooper.packetevents.util.Vector3i
import com.github.shynixn.mccoroutine.folia.regionDispatcher
import dev.slne.surf.api.core.messages.builder.SurfComponentBuilder
import dev.slne.surf.api.paper.util.forEachPlayer
import dev.slne.surf.parkour.api.data.ParkourRun
import dev.slne.surf.parkour.api.data.ParkourStats
import dev.slne.surf.parkour.core.paper.service.ParkourTexturesService
import dev.slne.surf.parkour.paper.plugin
import dev.slne.surf.parkour.paper.service.ParkourService
import kotlinx.coroutines.withContext
import org.bukkit.Location
import org.bukkit.Server
import org.bukkit.block.data.BlockData
import org.bukkit.util.Vector

inline fun <reified T> Triple<*, *, *>.anyOfType(predicate: (T) -> Boolean): Boolean {
    return listOf(first, second, third)
        .filterIsInstance<T>()
        .any(predicate)
}

inline fun <reified T> Triple<*, *, *>.allOfType(predicate: (T) -> Boolean): Boolean {
    val items = listOf(first, second, third).filterIsInstance<T>()
    if (items.isEmpty()) return false
    return items.all(predicate)
}


fun Vector.equalsVector3i(other: Vector3i): Boolean {
    return this.blockX == other.x &&
            this.blockY == other.y &&
            this.blockZ == other.z
}

/**
 * Checks if two vectors represent the same block position.
 * Note: This function compares Bukkit Vector with Bukkit Vector.
 * For comparing with PacketEvents Vector3i, use equalsVector3i() instead.
 */
fun Vector.isSameBlock(other: Vector): Boolean {
    return this.blockX == other.blockX &&
            this.blockY == other.blockY &&
            this.blockZ == other.blockZ
}

val Long.formattedDuration: String
    get() {
        val totalSeconds = this / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        val parts = mutableListOf<String>()

        if (hours > 0) parts += "${hours}h"
        if (minutes > 0) parts += "${minutes}m"
        if (seconds > 0 || parts.isEmpty()) parts += "${seconds}s"

        return parts.joinToString(" ")
    }

fun SurfComponentBuilder.appendLinePrefix() = darkSpacer("» ")

fun Server.sendBlockChange(location: Location, blockData: BlockData) = forEachPlayer {
    it.sendBlockChange(location, blockData)
}

suspend fun sendGlobalBlockChange(
    location: Location,
    blockData: BlockData
) = withContext(plugin.regionDispatcher(location)) {
    location.world.setBlockData(location, blockData)
}

val ParkourStats.playerName: String get() = ParkourTexturesService.getTexture(playerUuid).playerName
val ParkourRun.parkour get() = ParkourService.getParkour(this.parkourUuid)
