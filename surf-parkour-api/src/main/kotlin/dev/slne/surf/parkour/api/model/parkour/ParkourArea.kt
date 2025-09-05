package dev.slne.surf.parkour.api.model.parkour

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block

interface ParkourArea {
    val world: World
    val firstLocation: Location
    val secondLocation: Location

    fun contains(location: Location): Boolean
    fun contains(block: Block): Boolean
}
