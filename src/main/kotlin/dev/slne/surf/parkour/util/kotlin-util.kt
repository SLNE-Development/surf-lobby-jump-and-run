package dev.slne.surf.parkour.util

import com.github.retrooper.packetevents.util.Vector3i
import org.bukkit.util.Vector
import java.time.Instant
import java.time.format.DateTimeFormatter

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

private val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")

val Long.formattedTimeEpoch: String get() = formatter.format(Instant.ofEpochMilli(this))
