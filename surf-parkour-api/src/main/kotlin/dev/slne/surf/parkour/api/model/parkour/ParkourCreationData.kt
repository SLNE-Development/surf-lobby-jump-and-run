package dev.slne.surf.parkour.api.model.parkour

import org.bukkit.Location
import java.util.*

interface ParkourCreationData {
    val uuid: UUID
    val name: String
    val corner1: Location
    val corner2: Location
    val spawn: Location
}