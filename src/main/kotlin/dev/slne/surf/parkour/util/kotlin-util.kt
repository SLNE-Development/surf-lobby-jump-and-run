package dev.slne.surf.parkour.util

import com.github.retrooper.packetevents.util.Vector3i
import org.bukkit.util.Vector
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

inline fun <reified T> Triple<*, *, *>.anyOfType(predicate: (T) -> Boolean): Boolean {
    return listOf(first, second, third)
        .filterIsInstance<T>()
        .any(predicate)
}

fun Vector.equalsVector3i(other: Vector3i): Boolean {
    return this.blockX == other.x &&
            this.blockY == other.y &&
            this.blockZ == other.z
}

private val formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")

val Long.formattedTimeEpoch: String
    get() = Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .format(formatter)
