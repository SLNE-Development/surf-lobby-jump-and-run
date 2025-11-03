package dev.slne.surf.parkour.`object`

import com.github.shynixn.mccoroutine.folia.regionDispatcher
import dev.slne.surf.parkour.plugin
import dev.slne.surf.surfapi.core.api.util.random
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import java.util.*

data class ParkourGenerator(
    private val associatedPlayer: UUID,
    private val parkour: Parkour,
    private val material: Material
) {
    private lateinit var _blockLocations: Triple<Location, Location, Location>
    private val boundingBox = parkour.boundingBox
    private val _world = parkour.world

    suspend fun start() {

    }

    suspend fun stop() {

    }

    private suspend fun generateInitial() {

    }

    private suspend fun generate() {

    }

    private suspend fun findBock(
        maxTries: Int = 100
    ): Location? = withContext(Dispatchers.IO) {
        val minX = boundingBox.minX
        val maxX = boundingBox.maxX
        val minY = boundingBox.minY
        val maxY = boundingBox.maxY
        val minZ = boundingBox.minZ
        val maxZ = boundingBox.maxZ

        val midX = (minX + maxX) / 2
        val midY = (minY + maxY) / 2
        val midZ = (minZ + maxZ) / 2

        val rangeX = maxX - minX
        val rangeY = maxY - minY
        val rangeZ = maxZ - minZ

        val halfRangeX = (rangeX * 0.75 * 0.5).toInt()
        val halfRangeY = (rangeY * 0.75 * 0.5).toInt()
        val halfRangeZ = (rangeZ * 0.75 * 0.5).toInt()

        repeat(maxTries) {
            val x = (midX + random.nextInt(-halfRangeX, halfRangeX + 1))
                .coerceIn(minX, maxX)
            val y = (midY + random.nextInt(-halfRangeY, halfRangeY + 1))
                .coerceIn(minY, maxY)
            val z = (midZ + random.nextInt(-halfRangeZ, halfRangeZ + 1))
                .coerceIn(minZ, maxZ)

            val block = Location(_world, x, y, z).getContextBlock()
            val above = block.getRelative(BlockFace.UP, 1)
            val above2 = block.getRelative(BlockFace.UP, 2)

            if (above.isEmpty && above2.isEmpty) {
                return@withContext block.location
            }
        }

        return@withContext null
    }

    private suspend fun Location.getContextBlock() = withContext(plugin.regionDispatcher(this)) {
        val loc = this@getContextBlock
        return@withContext loc.world.getBlockAt(loc)
    }
}