package dev.slne.surf.parkour.paper.model.parkour

import com.github.shynixn.mccoroutine.folia.regionDispatcher
import dev.slne.surf.parkour.paper.model.jump.Jump
import dev.slne.surf.parkour.paper.model.jump.JumpType
import dev.slne.surf.parkour.paper.plugin
import dev.slne.surf.parkour.paper.util.getPlayer
import dev.slne.surf.parkour.paper.util.sendGlobalBlockChange
import dev.slne.surf.surfapi.bukkit.api.glow.glowingApi
import dev.slne.surf.surfapi.core.api.util.random
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.util.BoundingBox
import org.bukkit.util.Vector
import java.lang.Math.toDegrees
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.atan2
import kotlin.math.sqrt

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

    val player get() = associatedPlayer.getPlayer()

    var startTime: Long = -1L
    var currentIndex = 0

    private val isGenerating = AtomicBoolean(false)

    private val jumpTypes = listOf(
        JumpType(2..3, 2..3, -1..1),
        JumpType(2..3, -3..-2, -1..1)
    )

    suspend fun start() = withContext(Dispatchers.IO) {
        val player = associatedPlayer.getPlayer() ?: return@withContext
        startTime = System.currentTimeMillis()

        generateInitial()
        player.velocity = Vector(0, 0, 0)

        val rotation = calcRotation(blockLocations.first, blockLocations.second)
        val toTeleport = blockLocations.first.location.block.getRelative(BlockFace.UP).location

        toTeleport.setRotation(rotation.first, rotation.second)
        player.teleportAsync(toTeleport)
    }

    suspend fun stop() = withContext(Dispatchers.IO) {
        associatedPlayer.getPlayer()?.let {
            glowingApi.removeGlowing(blockLocations.second.location, it)
        }


        sendGlobalBlockChange(blockLocations.first.location, airData)
        sendGlobalBlockChange(blockLocations.second.location, airData)
        sendGlobalBlockChange(blockLocations.third.location, airData)

    }

    private suspend fun generateInitial() {
        val player = associatedPlayer.getPlayer() ?: return

        val otherPlayersBlocks = getOtherPlayersBlocks()

        val firstBlock = findInitialBlock()
        val secondJump = jumpTypes.random().randomJump()
        val secondBlock =
            secondJump.generate(firstBlock, firstBlock, player, boundingBox, otherPlayersBlocks)

        val thirdJump = jumpTypes.random().randomJump()
        val thirdBlock =
            thirdJump.generate(secondBlock, firstBlock, player, boundingBox, otherPlayersBlocks)

        sendGlobalBlockChange(firstBlock.location, material.createBlockData())
        sendGlobalBlockChange(secondBlock.location, material.createBlockData())
        sendGlobalBlockChange(thirdBlock.location, material.createBlockData())


        blockLocations = Triple(firstBlock, secondBlock, thirdBlock)
        glowingApi.makeGlowing(secondBlock.location, player, color)
    }

    suspend fun generate() = withContext(Dispatchers.IO) {
        if (!isGenerating.compareAndSet(false, true)) {
            return@withContext
        }

        try {
            val player = associatedPlayer.getPlayer() ?: return@withContext

            if (!::blockLocations.isInitialized) {
                return@withContext
            }

            currentIndex++

            val otherPlayersBlocks = getOtherPlayersBlocks()
            val newJump = jumpTypes.random().randomJump()
            val newNext = newJump.generate(
                blockLocations.third,
                blockLocations.second,
                player,
                boundingBox,
                otherPlayersBlocks
            )

            sendGlobalBlockChange(blockLocations.first.location, airData)
            sendGlobalBlockChange(newNext.location, material.createBlockData())

            glowingApi.removeGlowing(blockLocations.second.location, player)
            glowingApi.makeGlowing(blockLocations.third.location, player, color)

            blockLocations = Triple(
                blockLocations.second,
                blockLocations.third,
                newNext
            )
        } finally {
            isGenerating.set(false)
        }
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

    private fun getOtherPlayersBlocks(): List<Vector> {
        return parkour.generators
            .filter { it.associatedPlayer != associatedPlayer && it.isRunning() }
            .flatMap {
                listOf(
                    it.blockLocations.first,
                    it.blockLocations.second,
                    it.blockLocations.third
                )
            }
    }

    fun calcRotation(from: Vector, to: Vector): Pair<Float, Float> {
        val dir = to.clone().subtract(from)
        val dx = dir.x
        val dy = dir.y
        val dz = dir.z

        val yaw = toDegrees(atan2(dz, dx)) - 90
        val pitch = -toDegrees(atan2(dy, sqrt(dx * dx + dz * dz)))

        return Pair(yaw.toFloat(), pitch.toFloat())
    }

    fun isRunning() = ::blockLocations.isInitialized
}