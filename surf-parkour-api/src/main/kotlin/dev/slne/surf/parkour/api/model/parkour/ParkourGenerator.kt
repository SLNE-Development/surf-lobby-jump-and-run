package dev.slne.surf.parkour.api.model.parkour

import dev.slne.surf.parkour.api.entity.ParkourPlayer
import it.unimi.dsi.fastutil.objects.ObjectSet
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block

interface ParkourGenerator {
    val player: ParkourPlayer
    val material: Material
    val corner1: Location
    val corner2: Location
    val fallback: Location

    fun currentIndex(): Int = 0

    fun currentBlock(): Block?
    fun targetBlock(): Block?
    fun nextBlock(): Block?

    fun getRegisteredBlocks(): ObjectSet<Block>
    fun getNextBlocks(): ObjectSet<Block>

    suspend fun start()
    suspend fun stop()
    suspend fun generate()
    suspend fun generateInitial()
}