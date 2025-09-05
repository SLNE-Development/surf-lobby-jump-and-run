package dev.slne.surf.parkour.core.generator

import dev.slne.surf.parkour.api.entity.ParkourPlayer
import dev.slne.surf.parkour.api.model.parkour.ParkourGenerator
import dev.slne.surf.parkour.core.model.jump.Jumps
import dev.slne.surf.parkour.core.util.sendBlockChange
import dev.slne.surf.parkour.core.util.sendBlockChanges
import dev.slne.surf.surfapi.bukkit.api.glow.glowingApi
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import dev.slne.surf.surfapi.core.api.util.random
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block

class DefaultParkourGenerator(
    override val player: ParkourPlayer,
    override val material: Material,
    override val corner1: Location,
    override val corner2: Location,
    override val fallback: Location
) : ParkourGenerator {
    var currentBlock: Block? = null
    var targetBlock: Block? = null
    var nextBlock: Block? = null
    var currentIndex = 0

    override fun currentIndex() = currentIndex

    override fun currentBlock() = currentBlock
    override fun targetBlock() = targetBlock
    override fun nextBlock() = nextBlock
    override fun getRegisteredBlocks(): ObjectSet<Block> {
        val blocks = mutableObjectSetOf<Block>()
        currentBlock?.let { blocks.add(it) }
        targetBlock?.let { blocks.add(it) }
        nextBlock?.let { blocks.add(it) }
        return blocks
    }

    override fun getNextBlocks(): ObjectSet<Block> {
        val blocks = mutableObjectSetOf<Block>()
        targetBlock?.let { blocks.add(it) }
        nextBlock?.let { blocks.add(it) }
        return blocks
    }

    override suspend fun start() {
        val player = player.player() ?: return

        if (currentBlock != null || targetBlock != null || nextBlock != null) {
            player.sendText {
                appendPrefix()
                error("Ein Fehler ist aufgetreten. (PARKOUR_START_ALREADY_STARTED)")
            }
            return
        }

        if (corner1.world != corner2.world) {
            player.sendText {
                appendPrefix()
                error("Ein Fehler ist aufgetreten. (PARKOUR_START_DIFFERENT_WORLDS)")
            }
            return
        }

        this.generateInitial()

        currentBlock?.let {
            player.teleportAsync(it.location.clone().add(0.5, 1.0, 0.5))
        }
    }

    override suspend fun stop() {
        currentBlock?.let {
            Bukkit.getServer().sendBlockChange(it.location, Material.AIR)
        }

        targetBlock?.let {
            glowingApi.removeGlowing(it, player.player() ?: return)
            Bukkit.getServer().sendBlockChange(it.location, Material.AIR)
        }

        nextBlock?.let {
            Bukkit.getServer().sendBlockChange(it.location, Material.AIR)
        }
    }

    override suspend fun generateInitial() {
        val player = player.player() ?: return

        val currentBlock = findSafeBlockLocationInArea() ?: run {
            player.sendText {
                appendPrefix()
                error("Ein Fehler ist aufgetreten. (PARKOUR_INITIAL_NO_BLOCK)")
            }
            return
        }

        val targetJump = Jumps.entries.randomOrNull()?.jump ?: run {
            player.sendText {
                appendPrefix()
                error("Ein Fehler ist aufgetreten. (PARKOUR_INITIAL_NO_JUMPS_TARGET)")
            }
            return
        }
        val targetBlock = targetJump.generate(currentBlock.block, player)

        val nextJump = Jumps.entries.randomOrNull()?.jump ?: run {
            player.sendText {
                appendPrefix()
                error("Ein Fehler ist aufgetreten. (PARKOUR_INITIAL_NO_JUMPS_NEXT)")
            }
            return
        }
        val nextBlock = nextJump.generate(targetBlock, player)

        Bukkit.getServer().sendBlockChanges(
            currentBlock to Material.RED_CONCRETE,
            targetBlock.location to Material.RED_CONCRETE,
            nextBlock.location to Material.RED_CONCRETE
        )

        this.currentBlock = currentBlock.block
        this.targetBlock = targetBlock
        this.nextBlock = nextBlock

        glowingApi.makeGlowing(targetBlock, player, NamedTextColor.WHITE)
    }

    override suspend fun generate() {
        val player = player.player() ?: return

        if (currentBlock == null || targetBlock == null || nextBlock == null) {
            player.sendText {
                appendPrefix()
                error("Ein Fehler ist aufgetreten. (PARKOUR_GENERATE_NO_BLOCKS)")
            }
            return
        }

        val nextJump = Jumps.entries.randomOrNull()?.jump ?: run {
            player.sendText {
                appendPrefix()
                error("Ein Fehler ist aufgetreten. (PARKOUR_GENERATE_NO_JUMPS_NEXT)")
            }
            return
        }
        val nextBlock = nextJump.generate(nextBlock, player)

        currentBlock?.location?.let {
            Bukkit.getServer().sendBlockChange(it, Material.AIR)
        }

        targetBlock?.let {
            glowingApi.removeGlowing(it, player)
        }

        currentBlock = targetBlock
        targetBlock = this@DefaultParkourGenerator.nextBlock
        this.nextBlock = nextBlock

        Bukkit.getServer().sendBlockChange(nextBlock.location, Material.RED_CONCRETE)

        currentIndex++
        glowingApi.makeGlowing(nextBlock, player, NamedTextColor.WHITE)
    }

    fun findSafeBlockLocationInArea(
        maxTries: Int = 100
    ): Location? {
        val world = corner1.world

        if (corner2.world != world) {
            error("Corners must be in the same world")
        }

        val minX = minOf(corner1.blockX, corner2.blockX)
        val maxX = maxOf(corner1.blockX, corner2.blockX)
        val minY = minOf(corner1.blockY, corner2.blockY)
        val maxY = maxOf(corner1.blockY, corner2.blockY)
        val minZ = minOf(corner1.blockZ, corner2.blockZ)
        val maxZ = maxOf(corner1.blockZ, corner2.blockZ)

        val midY = (minY + maxY) / 2
        val rangeY = maxY - minY

        val halfRange = (rangeY * 0.5).toInt()

        repeat(maxTries) {
            val x = random.nextInt(minX, maxX + 1)
            val z = random.nextInt(minZ, maxZ + 1)
            val y = (midY + random.nextInt(-halfRange, halfRange + 1))
                .coerceIn(minY, maxY)

            val block = world.getBlockAt(x, y, z)
            val above = world.getBlockAt(x, y + 1, z)
            val above2 = world.getBlockAt(x, y + 2, z)

            if (block.type.isAir && above.type.isAir && above2.type.isAir) {
                return block.location
            }
        }

        return null
    }
}