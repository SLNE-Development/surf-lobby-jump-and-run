package dev.slne.surf.parkour.parkour

import com.github.shynixn.mccoroutine.bukkit.launch
import dev.slne.surf.parkour.SurfParkour

import dev.slne.surf.parkour.database.DatabaseProvider
import dev.slne.surf.parkour.instance
import dev.slne.surf.parkour.util.Area
import dev.slne.surf.parkour.util.Colors
import dev.slne.surf.parkour.util.MessageBuilder

import it.unimi.dsi.fastutil.objects.*

import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.util.Vector

import java.time.Duration

import java.util.UUID

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

data class Parkour (
    val uuid: UUID,
    var name: String,

    var world: World,
    var area: Area,
    var start: Vector,
    var respawn: Vector,

    val availableMaterials: ObjectSet<Material>,
    val activePlayers: ObjectSet<Player>
) {
    private val random = Random

    val blocks: Object2ObjectMap<Player, Material> = Object2ObjectOpenHashMap()
    val latestJumps: Object2ObjectMap<Player, Array<Block?>> = Object2ObjectOpenHashMap()
    val currentPoints: Object2ObjectMap<Player, Int> = Object2ObjectOpenHashMap()

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

    /**
     *
     * Main parkour functions
     *
     */

    fun start(player: Player) {
        instance.launch {
            val jumps = arrayOfNulls<Block>(3)
            val highscore = DatabaseProvider.getPlayerData(player.uniqueId).highScore

            activePlayers.add(player)

            latestJumps[player] = jumps
            currentPoints[player] = 0

            increaseTrys(player)
            generateInitial(player)

            if (highscore < 1) {
                player.sendMessage(Colors.PREFIX.append(Component.text("Du bist nun im Parkour. Springe so weit wie möglich, um einen Highscore aufzustellen!")))
                return@launch
            }

            player.sendMessage(Colors.PREFIX.append(Component.text("Du bist nun im Parkour. Springe so weit wie möglich, um deinen Highscore von $highscore zu brechen!")))
        }
    }

    fun cancel(player: Player) {
        val latest = latestJumps[player] ?: return

        for (block in latest) {
            if(block == null || block.type == Material.AIR) continue

            player.sendBlockChange(block.location, Material.AIR.createBlockData())
        }

        player.teleportAsync(Location(world, respawn.x, respawn.y, respawn.z))

        instance.launch {
            updateHighscore(player)

            currentPoints.remove(player)
            latestJumps.remove(player)
            activePlayers.remove(player)
        }
    }

    private fun generateInitial(player: Player) {
        val randomLocation = getRandomLocationInRegion(world) ?: return

        val start = randomLocation.add(0.0, 1.0, 0.0)
        val block = start.block
        val material = availableMaterials.random(random)

        player.sendBlockChange(block.location, material.createBlockData())
        latestJumps[player]!![0] = block

        val next = getValidBlock(start, player)

        player.sendBlockChange(next.location, Material.SEA_LANTERN.createBlockData())
        latestJumps[player]!![1] = next

        val next2 = getValidBlock(next.location, player)

        player.sendBlockChange(next2.location, material.createBlockData())
        latestJumps[player]!![2] = next2

        player.teleportAsync(block.location.add(0.5, 1.0, 0.5))
        blocks[player] = material
    }

    fun generate(player: Player) {
        val jumps = latestJumps[player] ?: return
        val material = blocks[player] ?: return

        if (jumps[0] != null) {
            val jump0 = jumps[0] ?: return

            player.sendBlockChange(jump0.location, Material.AIR.createBlockData())
        }

        jumps[0] = jumps[1]
        jumps[1] = jumps[2]

        val block1 = jumps[1] ?: return

        player.sendBlockChange(block1.location, Material.SEA_LANTERN.createBlockData())

        val nextJump = getValidBlock(block1.location, player)
        player.sendBlockChange(nextJump.location, material.createBlockData())
        jumps[2] = nextJump
    }

    /**
     *
     * Storage/Statistik functions
     *
     */

    suspend fun increaseTrys(player: Player) {
        val playerData = DatabaseProvider.getPlayerData(player.uniqueId)

        playerData.edit {
            trys += 1
        }
    }

    suspend fun increasePoints(player: Player) {
        val playerData = DatabaseProvider.getPlayerData(player.uniqueId)

        playerData.edit {
            points = (currentPoints[player] ?: 1) + 1
        }

        currentPoints.compute(player) { _: Player?, curPts: Int? -> if (curPts == null) 1 else curPts + 1 }

        if (playerData.likesSound) {
            player.playSound(Sound.sound(org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, Sound.Source.MASTER, 10f, 1f), Sound.Emitter.self())
        }
    }

    private suspend fun updateHighscore(player: Player) {
        val currentScore = currentPoints[player] ?: return
        val playerData = DatabaseProvider.getPlayerData(player.uniqueId)

        if (currentScore <= playerData.highScore) {
            return
        }

        playerData.edit {
            highScore = currentScore
        }

        if (playerData.likesSound) {
            player.playSound(Sound.sound(org.bukkit.Sound.ITEM_TOTEM_USE, Sound.Source.MASTER, 10f, 1f), Sound.Emitter.self())
        }

        player.showTitle(Title.title(MessageBuilder().primary("Record!").build(), MessageBuilder().info("Du hast einen neuen persönlichen Rekord aufgestellt.").build(), Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(2), Duration.ofSeconds(1))))
        SurfParkour.send(player, MessageBuilder().primary("Du hast deinen Highscore gebrochen! ").info("Dein neuer Highscore ist $currentScore").primary("!"))
    }

    /**
     *
     * Utility functions
     *
     */

    private fun getRandomLocationInRegion(world: World): Location? {
        val posOne = area.max
        val posTwo = area.min

        var minX = min(posOne.blockX.toDouble(), posTwo.blockX.toDouble()).toInt()
        var maxX = max(posOne.blockX.toDouble(), posTwo.blockX.toDouble()).toInt()
        val minY = min(posOne.blockY.toDouble(), posTwo.blockY.toDouble()).toInt()
        val maxY = max(posOne.blockY.toDouble(), posTwo.blockY.toDouble()).toInt()
        var minZ = min(posOne.blockZ.toDouble(), posTwo.blockZ.toDouble()).toInt()
        var maxZ = max(posOne.blockZ.toDouble(), posTwo.blockZ.toDouble()).toInt()

        val widthX = maxX - minX
        val heightY = maxY - minY
        val widthZ = maxZ - minZ

        if (widthX <= 20 || heightY <= 20 || widthZ <= 20) {
            Bukkit.getConsoleSender().sendMessage("Die Jump and Run Region ist zu klein.")
            return null
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
        val posOne = area.max
        val posTwo = area.min

        if (location.world != null) {
            if (location.world != world) {
                return false
            }
        }

        val minX = min(posOne.blockX.toDouble(), posTwo.blockX.toDouble()).toInt()
        val maxX = max(posOne.blockX.toDouble(), posTwo.blockX.toDouble()).toInt()
        val minY = min(posOne.blockY.toDouble(), posTwo.blockY.toDouble()).toInt()
        val maxY = max(posOne.blockY.toDouble(), posTwo.blockY.toDouble()).toInt()
        val minZ = min(posOne.blockZ.toDouble(), posTwo.blockZ.toDouble()).toInt()
        val maxZ = max(posOne.blockZ.toDouble(), posTwo.blockZ.toDouble()).toInt()

        return location.blockX in minX..maxX && location.blockY >= minY && location.blockY <= maxY && location.blockZ >= minZ && location.blockZ <= maxZ
    }

    private fun getValidBlock(previousLocation: Location, player: Player): Block {
        val maxAttempts = offsets.size * 2
        var attempts = 0

        while (attempts < maxAttempts) {
            val heightOffset = random.nextInt(3) - 1
            val offset = offsets[random.nextInt(offsets.size)]
            val nextLocation = previousLocation.clone().add(offset).add(0.0, heightOffset.toDouble(), 0.0)

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

            val latestPlayerJumps: Array<Block?> = latestJumps[player] ?: return previousLocation.clone().add(offsets[0]).block

            val block0: Block = latestPlayerJumps[0] ?: return previousLocation.clone().add(offsets[0]).block
            val block1: Block = latestPlayerJumps[1] ?: return previousLocation.clone().add(offsets[0]).block
            val block2: Block = latestPlayerJumps[2] ?: return previousLocation.clone().add(offsets[0]).block

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

            if (nextLocation == player.location || nextLocation == player.location.clone().add(0.0, 1.0, 0.0)) {
                attempts++
                continue
            }

            return nextLocation.block
        }
        return previousLocation.clone().add(offsets[0]).block
    }

    fun edit(block: Parkour.() -> Unit) {
        DatabaseProvider.getParkours().remove(this)
        this.apply(block)
        DatabaseProvider.getParkours().add(this)
    }

    /**
     *
     * Static Parkour functions
     *
     */

    companion object {
        fun getByName(name: String): Parkour? {
            return DatabaseProvider.getParkours().firstOrNull { it.name == name }
        }

        fun isJumping(player: Player): Boolean {
            return DatabaseProvider.getParkours().any { it.activePlayers.contains(player) }
        }

        fun getParkour(player: Player): Parkour? {
            return DatabaseProvider.getParkours().firstOrNull { it.activePlayers.contains(player) }
        }
    }
}