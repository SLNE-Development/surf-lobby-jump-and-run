package dev.slne.surf.parkour.core.client.model.geometry

/**
 * A position in a named world, independent of any server platform.
 *
 * @property world the name the world is known by on this server
 * @property x the x coordinate
 * @property y the y coordinate
 * @property z the z coordinate
 * @property yaw the yaw this position looks at
 * @property pitch the pitch this position looks at
 */
data class ParkourLocation(
    val world: String,
    val x: Double,
    val y: Double,
    val z: Double,
    val yaw: Float = 0f,
    val pitch: Float = 0f
) {
    /**
     * The point this location lies at, without its world or rotation.
     */
    fun toVector() = ParkourVector(x, y, z)

    /**
     * Returns a copy of this location looking at [yaw] and [pitch].
     */
    fun withRotation(yaw: Float, pitch: Float) = copy(yaw = yaw, pitch = pitch)
}
