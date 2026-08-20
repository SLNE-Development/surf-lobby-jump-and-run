package dev.slne.surf.parkour.core.client.model.geometry

import kotlin.math.floor
import kotlin.math.sqrt

/**
 * A point in a world, independent of any server platform.
 *
 * @property x the x coordinate
 * @property y the y coordinate
 * @property z the z coordinate
 */
data class ParkourVector(
    val x: Double,
    val y: Double,
    val z: Double
) {
    constructor(x: Int, y: Int, z: Int) : this(x.toDouble(), y.toDouble(), z.toDouble())

    /**
     * The x coordinate of the block this point lies in.
     */
    val blockX get() = floor(x).toInt()

    /**
     * The y coordinate of the block this point lies in.
     */
    val blockY get() = floor(y).toInt()

    /**
     * The z coordinate of the block this point lies in.
     */
    val blockZ get() = floor(z).toInt()

    /**
     * The length of this vector.
     */
    val length get() = sqrt(x * x + y * y + z * z)

    /**
     * Returns this vector scaled to a length of one.
     */
    fun normalize(): ParkourVector {
        val length = length
        return ParkourVector(x / length, y / length, z / length)
    }

    /**
     * Returns the sum of this vector and [other].
     */
    operator fun plus(other: ParkourVector) =
        ParkourVector(x + other.x, y + other.y, z + other.z)

    /**
     * Returns the difference between this vector and [other].
     */
    operator fun minus(other: ParkourVector) =
        ParkourVector(x - other.x, y - other.y, z - other.z)

    /**
     * Returns this vector scaled by [factor].
     */
    operator fun times(factor: Double) = ParkourVector(x * factor, y * factor, z * factor)

    /**
     * Returns a copy of this vector moved by [offset] along the y axis.
     */
    fun addY(offset: Double) = copy(y = y + offset)

    /**
     * Returns the distance between this vector and [other].
     */
    fun distance(other: ParkourVector): Double {
        val deltaX = other.x - x
        val deltaY = other.y - y
        val deltaZ = other.z - z

        return sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ)
    }

    /**
     * Returns whether this vector and [other] lie in the same block.
     */
    fun isSameBlock(other: ParkourVector) =
        blockX == other.blockX && blockY == other.blockY && blockZ == other.blockZ

    /**
     * Returns this vector snapped to the coordinates of the block it lies in.
     */
    fun toBlockVector() = ParkourVector(blockX, blockY, blockZ)

    /**
     * Returns the center of the block this vector lies in.
     */
    fun toBlockCenter() = ParkourVector(blockX + 0.5, blockY + 0.5, blockZ + 0.5)

    /**
     * Returns this vector as a location in the world [world] names.
     */
    fun toLocation(world: String, yaw: Float = 0f, pitch: Float = 0f) =
        ParkourLocation(world, x, y, z, yaw, pitch)
}
