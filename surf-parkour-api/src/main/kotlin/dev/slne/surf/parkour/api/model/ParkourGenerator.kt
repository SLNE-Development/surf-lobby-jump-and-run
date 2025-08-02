package dev.slne.surf.parkour.api.model

import dev.slne.surf.parkour.api.entity.ParkourPlayer
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block

interface ParkourGenerator {
    val player: ParkourPlayer
    val material: Material

    fun currentBlock(): Block?
    fun targetBlock(): Block?
    fun nextBlock(): Block?

    suspend fun start()
    suspend fun stop()
    suspend fun generate()
    suspend fun generateInitial(corner1: Location, corner2: Location)
}