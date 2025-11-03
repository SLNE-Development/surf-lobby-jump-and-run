package dev.slne.surf.parkour.core.generator

import dev.slne.surf.parkour.api.entity.ParkourPlayer
import dev.slne.surf.parkour.api.model.parkour.ParkourArea
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
    override val area: ParkourArea,
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
        val targetBlock = targetJump.generate(currentBlock.block, player, area)

        val nextJump = Jumps.entries.randomOrNull()?.jump ?: run {
            player.sendText {
                appendPrefix()
                error("Ein Fehler ist aufgetreten. (PARKOUR_INITIAL_NO_JUMPS_NEXT)")
            }
            return
        }
        val nextBlock = nextJump.generate(targetBlock, player, area)

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
        val nextBlock = nextJump.generate(nextBlock, player, area)

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
        targetBlock?.let {
            glowingApi.makeGlowing(it, player, NamedTextColor.WHITE)
        }
    }

    fun findSafeBlockLocationInArea(
        maxTries: Int = 100
    ): Location? {
        val minX = minOf(area.firstLocation.blockX, area.secondLocation.blockX)
        val maxX = maxOf(area.firstLocation.blockX, area.secondLocation.blockX)
        val minY = minOf(area.firstLocation.blockY, area.secondLocation.blockY)
        val maxY = maxOf(area.firstLocation.blockY, area.secondLocation.blockY)
        val minZ = minOf(area.firstLocation.blockZ, area.secondLocation.blockZ)
        val maxZ = maxOf(area.firstLocation.blockZ, area.secondLocation.blockZ)

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

            val block = area.world.getBlockAt(x, y, z)
            val above = area.world.getBlockAt(x, y + 1, z)
            val above2 = area.world.getBlockAt(x, y + 2, z)

            if (block.type.isAir && above.type.isAir && above2.type.isAir) {
                return block.location
            }
        }

        return null
    }

}