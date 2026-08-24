package dev.slne.surf.parkour.core.client.model.parkour

import dev.slne.surf.api.core.generated.BlockTypeKeys
import dev.slne.surf.api.core.util.random
import dev.slne.surf.parkour.core.client.model.geometry.*
import dev.slne.surf.parkour.core.client.model.jump.JumpType
import dev.slne.surf.parkour.core.client.platform.ParkourPlatform
import it.unimi.dsi.fastutil.longs.LongOpenHashSet
import it.unimi.dsi.fastutil.longs.LongSet
import it.unimi.dsi.fastutil.objects.ObjectList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.format.NamedTextColor
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 * The three blocks one player currently runs on, and how they move ahead.
 */
class ParkourGenerator(
    val associatedPlayer: UUID,
    private val parkour: Parkour,
    private val material: Key
) {
    /**
     * The block the player came from, the one they are heading for and the one after it, or `null`
     * while nothing has been built for them yet.
     */
    @Volatile
    var blockLocations: Triple<ParkourVector, ParkourVector, ParkourVector>? = null

    /**
     * Whether the player has already reached their target block and the next one is on its way.
     */
    private val advanced = AtomicBoolean(false)

    private val boundingBox: ParkourRegion = parkour.boundingBox
    private val world = parkour.world

    @Volatile
    var startTime: Long = -1L

    @Volatile
    var currentIndex = 0

    private val isGenerating = AtomicBoolean(false)

    /**
     * Claims the landing on the current target block, returning whether this call is the one that
     * may advance the run.
     */
    fun tryAdvance() = advanced.compareAndSet(false, true)

    suspend fun start() {
        ParkourPlatform.audience(associatedPlayer) ?: return
        startTime = System.currentTimeMillis()

        generateInitial()
        ParkourPlatform.resetVelocity(associatedPlayer)

        val blocks = blockLocations ?: return
        val rotation = calcRotation(blocks.first, blocks.second)
        val toTeleport = blocks.first.let {
            ParkourVector(it.blockX, it.blockY + 1, it.blockZ)
        }.toBlockCenter()

        ParkourPlatform.teleport(
            associatedPlayer,
            toTeleport.toLocation(world, rotation.first, rotation.second)
        )
    }

    suspend fun stop() {
        val blocks = blockLocations ?: return

        if (ParkourPlatform.audience(associatedPlayer) != null) {
            ParkourPlatform.clearHighlight(associatedPlayer, blocks.second.location)
        }

        setBlock(blocks.first, BlockTypeKeys.AIR)
        setBlock(blocks.second, BlockTypeKeys.AIR)
        setBlock(blocks.third, BlockTypeKeys.AIR)
    }

    private suspend fun generateInitial() {
        val yaw = ParkourPlatform.yaw(associatedPlayer) ?: return

        val occupiedColumns = collectOccupiedColumns()

        val firstBlock = findInitialBlock()
        val secondJump = JUMP_TYPES.random().randomJump()
        val secondBlock =
            secondJump.generate(firstBlock, firstBlock, yaw, boundingBox, occupiedColumns)

        val thirdJump = JUMP_TYPES.random().randomJump()
        val thirdBlock =
            thirdJump.generate(secondBlock, firstBlock, yaw, boundingBox, occupiedColumns)

        setBlock(firstBlock, material)
        setBlock(secondBlock, material)
        setBlock(thirdBlock, material)

        blockLocations = Triple(firstBlock, secondBlock, thirdBlock)
        ParkourPlatform.highlightBlock(associatedPlayer, secondBlock.location, COLOR)
    }

    suspend fun generate() {
        if (!isGenerating.compareAndSet(false, true)) {
            return
        }

        try {
            val yaw = ParkourPlatform.yaw(associatedPlayer) ?: return
            val blocks = blockLocations ?: return

            currentIndex++

            val occupiedColumns = collectOccupiedColumns()
            val newJump = JUMP_TYPES.random().randomJump()
            val newNext = newJump.generate(
                blocks.third,
                blocks.second,
                yaw,
                boundingBox,
                occupiedColumns
            )

            setBlock(blocks.first, BlockTypeKeys.AIR)
            setBlock(newNext, material)

            ParkourPlatform.clearHighlight(associatedPlayer, blocks.second.location)
            ParkourPlatform.highlightBlock(associatedPlayer, blocks.third.location, COLOR)

            blockLocations = Triple(
                blocks.second,
                blocks.third,
                newNext
            )
            advanced.set(false)
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

    /**
     * The columns the blocks of every other running player in this parkour occupy.
     */
    private fun collectOccupiedColumns(): LongSet {
        val generators = parkour.generators
        val columns = LongOpenHashSet(generators.size * 3)

        for (generator in generators.values) {
            if (generator.associatedPlayer == associatedPlayer) {
                continue
            }

            val blocks = generator.blockLocations ?: continue

            columns.add(blocks.first.blockColumn)
            columns.add(blocks.second.blockColumn)
            columns.add(blocks.third.blockColumn)
        }

        return columns
    }

    fun isRunning() = blockLocations != null

    companion object {
        /**
         * How many blocks above a spot have to be free for a player to stand on it.
         */
        private const val FREE_SPACE_ABOVE = 2

        private val COLOR = NamedTextColor.WHITE

        /**
         * The kinds of step a parkour is built from.
         */
        private val JUMP_TYPES: ObjectList<JumpType> = ObjectList.of(
            JumpType(2..3, 3..3, -1..1),
            JumpType(2..3, -3..-3, -1..1)
        )
    }
}
