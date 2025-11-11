package dev.slne.surf.parkour.model.parkour

import com.github.shynixn.mccoroutine.folia.regionDispatcher
import dev.slne.surf.parkour.model.jump.Jump
import dev.slne.surf.parkour.model.jump.JumpType
import dev.slne.surf.parkour.plugin
import dev.slne.surf.parkour.util.getPlayer
import dev.slne.surf.surfapi.bukkit.api.glow.glowingApi
import dev.slne.surf.surfapi.bukkit.api.util.forEachPlayer
import dev.slne.surf.surfapi.core.api.util.random
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.util.BoundingBox
import org.bukkit.util.Vector
import java.util.*

data class ParkourGenerator(
    val associatedPlayer: UUID,
    private val parkour: Parkour,
    private val material: Material
) {
    lateinit var blockLocations: Triple<Vector, Vector, Vector>

    private val boundingBox: BoundingBox = parkour.boundingBox
    private val _world = parkour.world
    private val color = NamedTextColor.WHITE
    private val airData = Material.AIR.createBlockData()

    var startTime: Long = -1L
    var currentIndex = 0

    private val jumpTypes = listOf(
        JumpType(2..3, 2..3, -1..1),
        JumpType(3..4, 2..4, 0..1)
    )

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

        val firstBlock = findInitialBlock()
        val secondJump = jumpTypes.random().randomJump()
        val secondBlock = secondJump.generate(firstBlock, player, boundingBox)

        val thirdJump = jumpTypes.random().randomJump()
        val thirdBlock = thirdJump.generate(secondBlock, player, boundingBox)

        forEachPlayer {
            it.sendBlockChange(firstBlock.location, material.createBlockData())
            it.sendBlockChange(secondBlock.location, material.createBlockData())
            it.sendBlockChange(thirdBlock.location, material.createBlockData())
        }

        blockLocations = Triple(firstBlock, secondBlock, thirdBlock)
        glowingApi.makeGlowing(secondBlock.location, player, color)
    }

    suspend fun generate() = withContext(Dispatchers.IO) {
        val player = associatedPlayer.getPlayer() ?: return@withContext
        currentIndex++

        if (!::blockLocations.isInitialized) {
            error("ParkourGenerator not started yet.")
        }

        val newJump = jumpTypes.random().randomJump()
        val newNext = newJump.generate(blockLocations.third, player, boundingBox)

        forEachPlayer {
            it.sendBlockChange(blockLocations.first.location, airData)
            it.sendBlockChange(newNext.location, material.createBlockData())
        }

        glowingApi.removeGlowing(blockLocations.second.location, player)
        glowingApi.makeGlowing(blockLocations.third.location, player, color)

        blockLocations = Triple(
            blockLocations.second,
            blockLocations.third,
            newNext
        )
    }

    private suspend fun findInitialBlock(): Vector = withContext(Dispatchers.IO) {
        val midX = (boundingBox.minX + boundingBox.maxX) / 2
        val midY = (boundingBox.minY + boundingBox.maxY) / 2
        val midZ = (boundingBox.minZ + boundingBox.maxZ) / 2

        val quarterX = ((boundingBox.maxX - boundingBox.minX) * 0.25 * 0.5).toInt()
        val quarterY = ((boundingBox.maxY - boundingBox.minY) * 0.25 * 0.5).toInt()
        val quarterZ = ((boundingBox.maxZ - boundingBox.minZ) * 0.25 * 0.5).toInt()

        repeat(100) {
            val x = (midX + random.nextInt(-quarterX, quarterX + 1)).coerceIn(
                boundingBox.minX,
                boundingBox.maxX
            )
            val y = (midY + random.nextInt(-quarterY, quarterY + 1)).coerceIn(
                boundingBox.minY,
                boundingBox.maxY
            )
            val z = (midZ + random.nextInt(-quarterZ, quarterZ + 1)).coerceIn(
                boundingBox.minZ,
                boundingBox.maxZ
            )

            val block = Location(_world, x, y, z).getContextBlock()
            val above = block.getRelative(BlockFace.UP)
            val above2 = above.getRelative(BlockFace.UP)
            if (above.isEmpty && above2.isEmpty) {
                return@withContext block.location.toVector()
            }
        }

        error("Failed to find safe block location in area")
    }

    private suspend fun Location.getContextBlock() = withContext(plugin.regionDispatcher(this)) {
        world.getBlockAt(this@getContextBlock)
    }

    private val Vector.location get() = Location(_world, x, y, z)

    private fun JumpType.randomJump() = Jump(
        forward.random(),
        lateral.random(),
        vertical.random()
    )
}