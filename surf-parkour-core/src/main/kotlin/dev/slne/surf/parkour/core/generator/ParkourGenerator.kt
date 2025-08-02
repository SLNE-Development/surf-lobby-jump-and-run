package dev.slne.surf.parkour.core.generator

import dev.slne.surf.parkour.api.entity.ParkourPlayer
import org.bukkit.Location

interface ParkourGenerator {
    val player: ParkourPlayer
    suspend fun start()
    suspend fun stop()
    suspend fun generate()
    suspend fun generateInitial(corner1: Location, corner2: Location)
}