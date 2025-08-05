package dev.slne.surf.parkour.api.model.parkour

import org.bukkit.Location

interface ParkourCreationData {
    val name: String
    val generator: ParkourGenerator
    val corner1: Location
    val corner2: Location
    val spawn: Location
}