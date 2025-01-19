package dev.slne.surf.lobby.jar.util

import dev.slne.surf.lobby.jar.player.JumpAndRunPlayer
import dev.slne.surf.lobby.jar.service.random
import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import kotlin.math.abs


class JumpGenerator(private val jnrPlayer: JumpAndRunPlayer) {

    val latestJumps: Array<Block?> = arrayOfNulls(3)
    val blocks = ObjectArrayList<Material>()

    private val offsets = arrayOf(
        Vector(3, 0, 0),
        Vector(-3, 0, 0),
        Vector(0, 0, 3),
        Vector(0, 0, -3),
        Vector(3, 0, 0),
        Vector(-3, 0, 0),
        Vector(0, 0, 3),
        Vector(0, 0, -3),
        Vector(3, 0, 3),
        Vector(-3, 0, 3),
        Vector(3, 0, 3),
        Vector(-3, 0, 3),
        Vector(3, 0, 0),
        Vector(0, 0, 3),
        Vector(-3, 0, 0)
    )

    fun generate() {
        val player = jnrPlayer.player ?: return
        val material = blocks[player] ?: return

        val jump0 = latestJumps[0]
        if (jump0 != null) {
            player.sendBlockChange(jump0.location, Material.AIR.createBlockData())
        }

        latestJumps[0] = latestJumps[1]
        latestJumps[1] = latestJumps[2]

        val block1 = latestJumps[1] ?: return

        player.sendBlockChange(block1.location, Material.SEA_LANTERN.createBlockData())

        val nextJump = getValidBlock(block1.location)
        player.sendBlockChange(nextJump.location, material.createBlockData())
        latestJumps[2] = nextJump
    }

    private fun getRandomLocationInRegion(world: World): Location {
        val boundingBox = jumpAndRun.boundingBox

        var minX = boundingBox.minX.toInt()
        var minZ = boundingBox.minZ.toInt()
        var maxZ = boundingBox.maxZ.toInt()
        var maxX = boundingBox.maxX.toInt()

        val minY = boundingBox.minY.toInt()
        val maxY = boundingBox.maxY.toInt()

        val widthX = boundingBox.widthX
        val heightY = boundingBox.height
        val widthZ = boundingBox.widthZ

        if (widthX <= 20 || heightY <= 20 || widthZ <= 20) {
            error("The jump and run region is too small.")
        }

        minX += 10
        maxX -= 10

        minZ += 10
        maxZ -= 10

        val x = random.nextInt(maxX - minX + 1) + minX
        val y = random.nextInt(maxY - minY + 1) + minY
        val z = random.nextInt(maxZ - minZ + 1) + minZ

        return Location(world, x.toDouble(), y.toDouble(), z.toDouble())
    }

    private fun isInRegion(location: Location): Boolean {
        val boundingBox = jumpAndRun.boundingBox

        if (location.world != jumpAndRun.world) {
            return false
        }

        return boundingBox.contains(location.toVector())
    }

    private fun getValidBlock(previousLocation: Location): Block {
        val player = jnrPlayer.player ?: return previousLocation.block

        val maxAttempts = offsets.size * 2
        var attempts = 0

        while (attempts < maxAttempts) {
            val heightOffset = random.nextInt(3) - 1
            val offset = offsets[random.nextInt(offsets.size)]
            val nextLocation =
                previousLocation.clone().add(offset).add(0.0, heightOffset.toDouble(), 0.0)

            if (!isInRegion(nextLocation)) {
                attempts++
                continue
            }

            if (nextLocation.block.type != Material.AIR || nextLocation.clone()
                    .add(0.0, 1.0, 0.0).block.type != Material.AIR || nextLocation.clone()
                    .add(0.0, 2.0, 0.0).block.type != Material.AIR
            ) {
                attempts++
                continue
            }

            val block0: Block =
                latestJumps[0] ?: return previousLocation.clone().add(offsets[0]).block
            val block1: Block =
                latestJumps[1] ?: return previousLocation.clone().add(offsets[0]).block
            val block2: Block =
                latestJumps[2] ?: return previousLocation.clone().add(offsets[0]).block

            /* Above the Jump */
            if (block0.location.clone().add(0.0, 1.0, 0.0) == nextLocation) {
                attempts++
                continue
            }
            if (block1.location.clone().add(0.0, 1.0, 0.0) == nextLocation) {
                attempts++
                continue
            }
            if (block2.location.clone().add(0.0, 1.0, 0.0) == nextLocation) {
                attempts++
                continue
            }

            /* 2 Blocks above the Jump */
            if (block0.location.clone().add(0.0, 2.0, 0.0) == nextLocation) {
                attempts++
                continue
            }

            if (block1.location.clone().add(0.0, 2.0, 0.0) == nextLocation) {
                attempts++
                continue
            }

            if (block2.location.clone().add(0.0, 2.0, 0.0) == nextLocation) {
                attempts++
                continue
            }

            if (abs(nextLocation.y - previousLocation.y) > 1) {
                attempts++
                continue
            }

            if (nextLocation == player.location || nextLocation == player.location.clone()
                    .add(0.0, 1.0, 0.0)
            ) {
                attempts++
                continue
            }

            return nextLocation.block
        }

        return previousLocation.clone().add(offsets[0]).block
    }

    fun generateInitialJumps() {
        val player = jnrPlayer.player ?: return
        val randomLocation = getRandomLocationInRegion(player.world)

        val start = randomLocation.add(0.0, 1.0, 0.0)
        val block = start.block
        val material = jumpAndRun.materials[random.nextInt(jumpAndRun.materials.size)]


        player.sendBlockChange(block.location, material.createBlockData())
        latestJumps[0] = block

        val next = getValidBlock(start)

        player.sendBlockChange(next.location, Material.SEA_LANTERN.createBlockData())
        latestJumps[1] = next

        val next2 = getValidBlock(next.location)

        player.sendBlockChange(next2.location, material.createBlockData())
        latestJumps[2] = next2

        player.teleportAsync(block.location.add(0.5, 1.0, 0.5))
        blocks[player] = material
    }
}