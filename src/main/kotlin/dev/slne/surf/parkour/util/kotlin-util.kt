package dev.slne.surf.parkour.util

import com.github.retrooper.packetevents.util.Vector3i
import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
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
