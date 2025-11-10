package dev.slne.surf.parkour.util

import com.github.retrooper.packetevents.util.Vector3i
import dev.slne.surf.surfapi.core.api.util.dateTimeFormatter
import org.bukkit.util.Vector
import java.time.Instant

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

val Long.formattedMillis: String get() = dateTimeFormatter.format(Instant.ofEpochMilli(this))
