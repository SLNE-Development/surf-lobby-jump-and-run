package dev.slne.surf.parkour.paper.util

import dev.slne.surf.parkour.core.client.model.geometry.ParkourLocation
import dev.slne.surf.parkour.core.client.model.geometry.ParkourRegion
import dev.slne.surf.parkour.core.client.model.geometry.ParkourVector
import org.bukkit.Location
import org.bukkit.util.BoundingBox

fun BoundingBox.toParkourRegion() = ParkourRegion.of(
    ParkourVector(minX, minY, minZ),
    ParkourVector(maxX, maxY, maxZ)
)

fun Location.toParkourLocation() = ParkourLocation(
    world = world.name,
    x = x,
    y = y,
    z = z,
    yaw = yaw,
    pitch = pitch
)
