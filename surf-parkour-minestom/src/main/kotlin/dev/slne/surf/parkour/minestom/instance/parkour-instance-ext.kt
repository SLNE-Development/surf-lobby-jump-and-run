package dev.slne.surf.parkour.minestom.instance

import dev.slne.minestom.lobby.api.extension.InstanceManager
import dev.slne.minestom.lobby.api.instance.worldKey
import dev.slne.surf.parkour.core.client.model.geometry.ParkourLocation
import dev.slne.surf.parkour.core.client.model.geometry.ParkourVector
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos
import net.minestom.server.instance.Instance

/**
 * The name a parkour addresses this instance's world by.
 */
val Instance.parkourWorldName: String? get() = worldKey?.value()

/**
 * Returns the loaded instance holding the world named [world].
 *
 * A world is addressable both by its plain name and by the full key it was loaded under, so a
 * config written on another platform keeps working.
 */
fun findParkourInstance(world: String): Instance? = InstanceManager.instances.firstOrNull {
    val key = it.worldKey ?: return@firstOrNull false
    key.value() == world || key.asString() == world
}

/**
 * The instance this location lies in, or `null` while its world is not loaded.
 */
fun ParkourLocation.instance(): Instance? = findParkourInstance(world)

/**
 * This location as a Minestom position.
 */
fun ParkourLocation.toPos() = Pos(x, y, z, yaw, pitch)

/**
 * This point as a platform-neutral parkour vector.
 */
fun Point.toParkourVector() = ParkourVector(x(), y(), z())
