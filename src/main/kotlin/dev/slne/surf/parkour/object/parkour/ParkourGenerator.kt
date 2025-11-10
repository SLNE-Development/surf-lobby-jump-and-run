package dev.slne.surf.parkour.`object`.parkour

import com.github.shynixn.mccoroutine.folia.regionDispatcher
import dev.slne.surf.parkour.`object`.jump.JumpType
import dev.slne.surf.parkour.plugin
import dev.slne.surf.parkour.util.getPlayer
import dev.slne.surf.surfapi.bukkit.api.glow.glowingApi
import dev.slne.surf.surfapi.bukkit.api.util.forEachPlayer
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.util.random
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.util.Vector
import java.util.*

data class ParkourGenerator(
    val associatedPlayer: UUID,
    private val parkour: Parkour,
    private val material: Material
) {
    lateinit var blockLocations: Triple<Vector, Vector, Vector> // block below player | target jump/block | next target jump/block

    private val boundingBox = parkour.boundingBox
    private val _world = parkour.world
    private val color = NamedTextColor.WHITE
    private val airData = Material.AIR.createBlockData()

    var startTime: Long = -1L
    var currentIndex = 0

    suspend fun start() = withContext(Dispatchers.IO) {
        val player = associatedPlayer.getPlayer() ?: return@withContext

        startTime = System.currentTimeMillis()

        generateInitial()
        player.teleportAsync(blockLocations.first.location.block.getRelative(BlockFace.UP).location)
    }

    suspend fun stop() = withContext(Dispatchers.IO) {
        val player = associatedPlayer.getPlayer() ?: return@withContext

        glowingApi.removeGlowing(blockLocations.second.location, player)

        forEachPlayer {
            it.sendBlockChange(blockLocations.first.location, airData)
            it.sendBlockChange(blockLocations.second.location, airData)
            it.sendBlockChange(blockLocations.third.location, airData)
        }
    }

    private suspend fun generateInitial() {
        val player = associatedPlayer.getPlayer() ?: return
        val currentBlock = findBock()
        val nextJump = JumpType.entries.random()
        val nextLocation = nextJump.jump.generate(currentBlock.toVector(), player, boundingBox)
        val nextOneJump = JumpType.entries.random()
        val nextOneLocation = nextOneJump.jump.generate(nextLocation, player, boundingBox)


        forEachPlayer {
            it.sendBlockChange(currentBlock, material.createBlockData())
            it.sendBlockChange(nextLocation.toLocation(_world), material.createBlockData())
            it.sendBlockChange(nextOneLocation.toLocation(_world), material.createBlockData())
        }

        blockLocations = Triple(
            currentBlock.toVector(),
            nextLocation,
            nextOneLocation
        )

        glowingApi.makeGlowing(nextLocation.location, player, color)
    }

    private var oldNextOne: JumpType? = null

    suspend fun generate() = withContext(Dispatchers.IO) {
        val player = associatedPlayer.getPlayer() ?: return@withContext

        if (!::blockLocations.isInitialized) {
            error("ParkourGenerator not started yet.")
        }

        player.sendText {
            appendPrefix()
            success("${oldNextOne?.jump}")
        }

        val newJump = JumpType.entries.random()
        val newNextOne = newJump.jump.generate(blockLocations.third, player, boundingBox)

        oldNextOne = newJump

        forEachPlayer {
            it.sendBlockChange(blockLocations.first.location, airData)
            it.sendBlockChange(newNextOne.location, material.createBlockData())
        }

        glowingApi.removeGlowing(blockLocations.second.location, player)
        glowingApi.makeGlowing(blockLocations.third.location, player, color)

        blockLocations = Triple(
            blockLocations.second,
            blockLocations.third,
            newNextOne
        )
    }

    private suspend fun findBock(): Location = withContext(Dispatchers.IO) {
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

        var tries = 0

        while (tries < 100) {
            tries++
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

        error("Failed to find safe block location in area after #$tries tries.")
    }

    private suspend fun Location.getContextBlock() = withContext(plugin.regionDispatcher(this)) {
        val loc = this@getContextBlock
        return@withContext loc.world.getBlockAt(loc)
    }

    private val Vector.location get() = Location(_world, x, y, z)
}