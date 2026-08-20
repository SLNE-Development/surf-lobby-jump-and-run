package dev.slne.surf.parkour.core.client.model.geometry

import kotlin.math.max
import kotlin.math.min

/**
 * An axis-aligned box, independent of any server platform.
 *
 * The corner coordinates are always ordered, so [minX] never exceeds [maxX] and so on.
 */
data class ParkourRegion(
    val minX: Double,
    val minY: Double,
    val minZ: Double,
    val maxX: Double,
    val maxY: Double,
    val maxZ: Double
) {
    /**
     * The corner with the lowest coordinates.
     */
    val min get() = ParkourVector(minX, minY, minZ)

    /**
     * The corner with the highest coordinates.
     */
    val max get() = ParkourVector(maxX, maxY, maxZ)

    /**
     * Returns [vector] with every coordinate pulled into this box.
     */
    fun clamp(vector: ParkourVector) = ParkourVector(
        vector.x.coerceIn(minX, maxX),
        vector.y.coerceIn(minY, maxY),
        vector.z.coerceIn(minZ, maxZ)
    )

    companion object {
        /**
         * Returns the box spanned by [first] and [second], whichever corner is which.
         */
        fun of(first: ParkourVector, second: ParkourVector) = ParkourRegion(
            minX = min(first.x, second.x),
            minY = min(first.y, second.y),
            minZ = min(first.z, second.z),
            maxX = max(first.x, second.x),
            maxY = max(first.y, second.y),
            maxZ = max(first.z, second.z)
        )
    }
}
