package dev.slne.surf.parkour.core.client.model.parkour

import dev.slne.surf.api.core.generated.BlockTypeKeys
import dev.slne.surf.api.core.util.random
import dev.slne.surf.parkour.core.client.model.geometry.ParkourLocation
import dev.slne.surf.parkour.core.client.model.geometry.ParkourRegion
import dev.slne.surf.parkour.core.client.model.geometry.ParkourVector
import dev.slne.surf.parkour.core.client.model.geometry.calcRotation
import dev.slne.surf.parkour.core.client.model.jump.JumpType
import dev.slne.surf.parkour.core.client.platform.ParkourPlatform
import it.unimi.dsi.fastutil.objects.ObjectList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.format.NamedTextColor
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

data class ParkourGenerator(
    val associatedPlayer: UUID,
    private val parkour: Parkour,
    private val material: Key
) {
    lateinit var blockLocations: Triple<ParkourVector, ParkourVector, ParkourVector>

    var advanced: Boolean = false

    private val boundingBox: ParkourRegion = parkour.boundingBox
    private val world = parkour.world
    private val color = NamedTextColor.WHITE

    var startTime: Long = -1L
    var currentIndex = 0

    private val isGenerating = AtomicBoolean(false)

    private val jumpTypes = ObjectList.of(
        JumpType(2..3, 3..3, -1..1),
        JumpType(2..3, -3..-3, -1..1)
    )

    suspend fun start() {
        ParkourPlatform.audience(associatedPlayer) ?: return
        startTime = System.currentTimeMillis()

        generateInitial()
        ParkourPlatform.resetVelocity(associatedPlayer)

        val rotation = calcRotation(blockLocations.first, blockLocations.second)
        val toTeleport = blockLocations.first.let {
            ParkourVector(it.blockX, it.blockY + 1, it.blockZ)
        }.toBlockCenter()

        ParkourPlatform.teleport(
            associatedPlayer,
            toTeleport.toLocation(world, rotation.first, rotation.second)
        )
    }

    suspend fun stop() {
        if (ParkourPlatform.audience(associatedPlayer) != null) {
            ParkourPlatform.clearHighlight(associatedPlayer, blockLocations.second.location)
        }

        setBlock(blockLocations.first, BlockTypeKeys.AIR)
        setBlock(blockLocations.second, BlockTypeKeys.AIR)
        setBlock(blockLocations.third, BlockTypeKeys.AIR)
    }

    private suspend fun generateInitial() {
        val yaw = ParkourPlatform.yaw(associatedPlayer) ?: return

        val otherPlayersBlocks = getOtherPlayersBlocks()

        val firstBlock = findInitialBlock()
        val secondJump = jumpTypes.random().randomJump()
        val secondBlock =
            secondJump.generate(firstBlock, firstBlock, yaw, boundingBox, otherPlayersBlocks)

        val thirdJump = jumpTypes.random().randomJump()
        val thirdBlock =
            thirdJump.generate(secondBlock, firstBlock, yaw, boundingBox, otherPlayersBlocks)

        setBlock(firstBlock, material)
        setBlock(secondBlock, material)
        setBlock(thirdBlock, material)

        blockLocations = Triple(firstBlock, secondBlock, thirdBlock)
        ParkourPlatform.highlightBlock(associatedPlayer, secondBlock.location, color)
    }

    suspend fun generate() {
        if (!isGenerating.compareAndSet(false, true)) {
            return
        }

        try {
            val yaw = ParkourPlatform.yaw(associatedPlayer) ?: return

            if (!::blockLocations.isInitialized) {
                return
            }

            currentIndex++

            val otherPlayersBlocks = getOtherPlayersBlocks()
            val newJump = jumpTypes.random().randomJump()
            val newNext = newJump.generate(
                blockLocations.third,
                blockLocations.second,
                yaw,
                boundingBox,
                otherPlayersBlocks
            )

            setBlock(blockLocations.first, BlockTypeKeys.AIR)
            setBlock(newNext, material)

            ParkourPlatform.clearHighlight(associatedPlayer, blockLocations.second.location)
            ParkourPlatform.highlightBlock(associatedPlayer, blockLocations.third.location, color)

            blockLocations = Triple(
                blockLocations.second,
                blockLocations.third,
                newNext
            )
            advanced = false
        } finally {
            isGenerating.set(false)
        }
    }

    private suspend fun findInitialBlock(): ParkourVector = withContext(Dispatchers.IO) {
        val midX = (boundingBox.minX + boundingBox.maxX) / 2
        val midY = (boundingBox.minY + boundingBox.maxY) / 2
        val midZ = (boundingBox.minZ + boundingBox.maxZ) / 2

        val halfX = ((boundingBox.maxX - boundingBox.minX) * 0.5).toInt()
        val halfY = ((boundingBox.maxY - boundingBox.minY) * 0.5).toInt()
        val halfZ = ((boundingBox.maxZ - boundingBox.minZ) * 0.5).toInt()

        repeat(100) {
            val x = (midX + random.nextInt(-halfX, halfX + 1)).coerceIn(
                boundingBox.minX,
                boundingBox.maxX
            )
            val y = (midY + random.nextInt(-halfY, halfY + 1)).coerceIn(
                boundingBox.minY,
                boundingBox.maxY
            )
            val z = (midZ + random.nextInt(-halfZ, halfZ + 1)).coerceIn(
                boundingBox.minZ,
                boundingBox.maxZ
            )

            val block = ParkourVector(x, y, z).toBlockVector()

            if (ParkourPlatform.hasFreeSpaceAbove(block.location, FREE_SPACE_ABOVE)) {
                return@withContext block
            }
        }

        error("Failed to find safe block location in area")
    }

    private suspend fun setBlock(vector: ParkourVector, type: Key) =
        ParkourPlatform.setBlock(vector.location, type)

    private val ParkourVector.location: ParkourLocation get() = toLocation(world)

    private fun getOtherPlayersBlocks(): List<ParkourVector> {
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

    fun isRunning() = ::blockLocations.isInitialized

    companion object {
        /**
         * How many blocks above a spot have to be free for a player to stand on it.
         */
        private const val FREE_SPACE_ABOVE = 2
    }
}
