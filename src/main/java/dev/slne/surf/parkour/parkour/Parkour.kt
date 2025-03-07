package dev.slne.surf.parkour.parkour

import dev.jorel.commandapi.wrappers.Rotation
import dev.slne.surf.parkour.SurfParkour
import dev.slne.surf.parkour.database.DatabaseProvider
import dev.slne.surf.parkour.plugin
import dev.slne.surf.parkour.util.Area
import dev.slne.surf.parkour.util.Colors
import dev.slne.surf.parkour.util.MessageBuilder
import dev.slne.surf.surfapi.core.api.util.random
import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.bukkit.*
import org.bukkit.block.Block

import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.time.Duration
import java.util.*
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.min
import kotlin.random.asKotlinRandom

data class Parkour(
    val uuid: UUID,
    var name: String,

    var world: World,
    var area: Area,
    var start: Vector,
    var respawn: Vector,

    val availableMaterials: ObjectSet<Material>,
    val activePlayers: ObjectSet<Player>
) {
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

    suspend fun startParkour(player: Player) {
        this.cancelParkour(player)

        val jumps = arrayOfNulls<Block>(3)

        activePlayers.add(player)

        latestJumps[player] = jumps
        currentPoints[player] = 0

        increaseTries(player)
        generateInitial(player)
    }

    suspend fun cancelParkour(player: Player) {
        val latest = latestJumps[player] ?: return

        for (block in latest) {
            val blockApi = plugin.blockApi ?: return
            if (block == null) continue

            blockApi.unsetGlowing(block, player)
            updateBlock(player, block.location, Material.AIR)
        }
        player.teleportAsync(Location(world, respawn.x, respawn.y, respawn.z))

        updateHighscore(player)
        announceNewHighscore(player)

        currentPoints.remove(player)
        latestJumps.remove(player)
        activePlayers.remove(player)
    }

    private suspend fun generateInitial(player: Player) {
        val blockApi = plugin.blockApi ?: return
        val randomLocation = getRandomLocationInRegion(player, world) ?: return

        val start = randomLocation.add(0.0, 1.0, 0.0)
        val block = start.block
        val material = availableMaterials.random(random.asKotlinRandom())

        updateBlock(player, block.location, material)
        latestJumps[player]!![0] = block

        val next = getValidBlock(start, player)

        updateBlock(player, next.location, material)
        blockApi.setGlowing(next.location, player, ChatColor.WHITE)

        latestJumps[player]!![1] = next

        val next2 = getValidBlock(next.location, player)

        updateBlock(player, next2.location, material)
        latestJumps[player]!![2] = next2

        // scored point

        val rotation = this.getRotation(next.location)

        player.teleportAsync(block.location.add(0.5, 1.0, 0.5).setRotation(rotation.yaw, rotation.pitch))
        blocks[player] = material
    }

    fun generate(player: Player) {
        val blockApi = plugin.blockApi ?: return
        val jumps = latestJumps[player] ?: return
        val material = blocks[player] ?: return

        if (jumps[0] != null) {
            val jump0 = jumps[0] ?: return

            updateBlock(player, jump0.location, Material.AIR)
        }

        jumps[0] = jumps[1]
        jumps[1] = jumps[2]

        val block1 = jumps[1] ?: return
        val block0 = jumps[0] ?: return

        updateBlock(player, block1.location, material)
        blockApi.setGlowing(block1.location, player, ChatColor.WHITE)
        blockApi.unsetGlowing(block0.location, player)

        val nextJump = getValidBlock(block1.location, player)

        updateBlock(player, nextJump.location, material)
        jumps[2] = nextJump
    }

    /**
     * player feedback
     *
     */

    suspend fun announceNewParkourStarted(player: Player, parkour: String) {
        val playerData = DatabaseProvider.getPlayerData(player.uniqueId)
        if (playerData.likesSound) {
            player.playSound(
                Sound.sound(org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, Sound.Source.MASTER, 10f, 1f),
                Sound.Emitter.self()
            )
        }
        SurfParkour.send(player, MessageBuilder().primary("Du hast den Parkour ").info(parkour).success(" gestartet").primary("!"))
    }

    suspend fun announceNewScoredPoint(player: Player) {
        val playerData = DatabaseProvider.getPlayerData(player.uniqueId)
        val jumpCount = currentPoints[player] ?: 1
        val highscore = playerData.highScore
        if (playerData.likesSound) {
            player.playSound(
                Sound.sound(org.bukkit.Sound.ENTITY_CHICKEN_EGG, Sound.Source.MASTER, 10f, 1f),
                Sound.Emitter.self()
            )
        }
        player.sendActionBar(
            Component.text("Rekord: $highscore Sprünge", Colors.GOLD).append(Component.text(" | ", Colors.SPACER))
                .append(Component.text("Aktuelle Sprünge: $jumpCount", Colors.GOLD))
        )
    }

    suspend fun announceParkourLoose(player: Player) {
        val playerData = DatabaseProvider.getPlayerData(player.uniqueId)
        val currentScore = currentPoints[player] ?: return

        if (playerData.likesSound) {
            player.playSound(
                Sound.sound(org.bukkit.Sound.ENTITY_VILLAGER_NO, Sound.Source.MASTER, 10f, 1f),
                Sound.Emitter.self()
            )
        }
        SurfParkour.send(
            player,
            MessageBuilder()
                .primary("Du hast den Parkour mit ")
                .variableValue("$currentScore Sprüngen")
                .primary(" beendet.")
        )
    }

    private suspend fun announceNewHighscore(player: Player) {
        val playerData = DatabaseProvider.getPlayerData(player.uniqueId)
        val currentScore = currentPoints[player] ?: return
        val highscore = playerData.highScore

        if (currentScore < highscore) {
            return
        }

        if (playerData.likesSound) {
            player.playSound(
                Sound.sound(org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, Sound.Source.MASTER, 10f, 1f),
                Sound.Emitter.self()
            )
        }

        player.showTitle(
            Title.title(
                MessageBuilder().component(Component.text("Herzlichen Glückwunsch!", Colors.GOLD)).build(),
                MessageBuilder().info("Du hast einen neuen Rekord aufgestellt!").build(),
                Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(2), Duration.ofSeconds(1))
            )
        )
        SurfParkour.send(
            player, MessageBuilder()
                .primary("Du hast deinen Highscore gebrochen! ")
                .newLine()
                .withPrefix()
                .primary("Dein neuer Highscore liegt nun bei")
                .component(Component.text(" $currentScore Sprüngen", Colors.GOLD))
                .primary("!")
        )
    }

    /**
     *
     * Storage/Statistik functions
     *
     */

    private suspend fun increaseTries(player: Player) {
        val playerData = DatabaseProvider.getPlayerData(player.uniqueId)

        playerData.edit {
            trys += 1
        }
    }

    suspend fun increasePoints(player: Player) {
        val playerData = DatabaseProvider.getPlayerData(player.uniqueId)

        currentPoints.compute(player) { _: Player?, curPts: Int? -> if (curPts == null) 1 else curPts + 1 }

        playerData.edit {points++}


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
    }

    /**
     *
     * Utility functions
     *
     */

    private suspend fun getRandomLocationInRegion(player: Player, world: World): Location? {
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
            Bukkit.getConsoleSender().sendMessage("Could not find random location in region because it is to small.. Parkour cancelled..")
            cancelParkour(player)
            SurfParkour.send(player, MessageBuilder().error("Es ist ein Fehler aufgetreten!"))
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

    private fun getRotation(next: Location): Rotation {
        val direction = next.toVector().subtract(start).normalize()
        val yaw = Math.toDegrees(atan2(-direction.x, direction.z)).toFloat()

        return Rotation(yaw, 0.0f)
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

            val latestPlayerJumps: Array<Block?> =
                latestJumps[player] ?: return previousLocation.clone().add(offsets[0]).block

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

    private fun updateBlock(player: Player, blockLocation: Location, block: Material) {
        player.sendBlockChange(blockLocation, block.createBlockData())
    }

    /**
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