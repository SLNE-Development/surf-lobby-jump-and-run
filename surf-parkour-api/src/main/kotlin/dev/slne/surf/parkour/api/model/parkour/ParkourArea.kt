package dev.slne.surf.parkour.api.model.parkour

import org.bukkit.Location
import org.bukkit.World

interface ParkourArea {
    val world: World
    val firstLocation: Location
    val secondLocation: Location
}
