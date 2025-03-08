package dev.slne.surf.parkour.parkour

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import com.github.shynixn.mccoroutine.folia.regionDispatcher
import dev.jorel.commandapi.wrappers.Rotation
import dev.slne.surf.parkour.SurfParkour
import dev.slne.surf.parkour.database.DatabaseProvider
import dev.slne.surf.parkour.plugin
import dev.slne.surf.parkour.util.Area
import dev.slne.surf.parkour.util.Colors
import dev.slne.surf.parkour.util.MessageBuilder
import dev.slne.surf.surfapi.core.api.messages.adventure.Sound
import dev.slne.surf.surfapi.core.api.messages.adventure.Title
import dev.slne.surf.surfapi.core.api.messages.adventure.appendText
import dev.slne.surf.surfapi.core.api.util.mutableObject2IntMapOf
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import dev.slne.surf.surfapi.core.api.util.random
import it.unimi.dsi.fastutil.objects.ObjectSet
import kotlinx.coroutines.withContext
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.util.*
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.min
import kotlin.random.asKotlinRandom
import kotlin.time.Duration.Companion.seconds

private const val NO_CURRENT_POINTS = -1

data class Parkour(
    val uuid: UUID,
    var name: String,

    var world: World,
    var area: Area,
    var start: Vector,
    var respawn: Vector,

    val availableMaterials: ObjectSet<Material>,
    val activePlayers: ObjectSet<UUID> = mutableObjectSetOf()
) {
    val blocks = mutableObject2ObjectMapOf<UUID, Material>()
    val latestJumps = mutableObject2ObjectMapOf<UUID, Array<Block?>>()
    val currentPoints =
        mutableObject2IntMapOf<UUID>().apply { defaultReturnValue(NO_CURRENT_POINTS) }

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

        val uuid = player.uniqueId
        activePlayers.add(uuid)

        latestJumps[uuid] = jumps
        currentPoints[uuid] = 0

        increaseTries(player)
        generateInitial(player)
    }

    suspend fun cancelParkour(player: Player) {
        val uuid = player.uniqueId
        val latest = latestJumps[uuid] ?: return

        for (block in latest) {
            val blockApi = plugin.blockApi
            if (block == null) continue

            blockApi.unsetGlowing(block, player)
            updateBlock(player, block.location, Material.AIR)
        }

        player.teleportAsync(Location(world, respawn.x, respawn.y, respawn.z))

        updateHighscore(player)
        announceNewHighscore(player)

        currentPoints.removeInt(uuid)
        latestJumps.remove(uuid)
        activePlayers.remove(uuid)
    }

    private suspend fun generateInitial(player: Player) {
        val blockApi = plugin.blockApi
        val randomLocation = getRandomLocationInRegion(player, world) ?: return
        val uuid = player.uniqueId

        val start = randomLocation.add(0.0, 1.0, 0.0)
        withContext(plugin.regionDispatcher(start)) {
            val block = start.block
            val material = availableMaterials.random(random.asKotlinRandom())

            updateBlock(player, block.location, material)
            latestJumps[uuid]!![0] = block

            val next = getValidBlock(start, player)

            updateBlock(player, next.location, material)
            blockApi.setGlowing(next.location, player, ChatColor.WHITE)

            latestJumps[uuid]!![1] = next

            val next2 = getValidBlock(next.location, player)

            updateBlock(player, next2.location, material)
            latestJumps[uuid]!![2] = next2

            // scored point

            val rotation = getRotation(next.location)

            player.teleportAsync(
                block.location.add(0.5, 1.0, 0.5).setRotation(rotation.yaw, rotation.pitch)
            )
            blocks[uuid] = material
        }
    }

    suspend fun generate(player: Player) {
        val blockApi = plugin.blockApi
        val uuid = player.uniqueId
        val jumps = latestJumps[uuid] ?: return
        val material = blocks[uuid] ?: return

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
            withContext(plugin.entityDispatcher(player)) {
                player.playSound(Sound {
                    type(org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING)
                    source(Sound.Source.MASTER)
                    volume(10f)
                    pitch(1f)
                }, Sound.Emitter.self())
            }

        }
        SurfParkour.send(player, MessageBuilder().primary("Du hast den Parkour ").info(parkour).success(" gestartet").primary("!"))
    }

    suspend fun announceNewScoredPoint(player: Player) {
        val uuid = player.uniqueId
        val playerData = DatabaseProvider.getPlayerData(uuid)
        val jumpCount = currentPoints.getOrDefault(uuid, 1)
        val highscore = playerData.highScore

        if (playerData.likesSound) {
            withContext(plugin.entityDispatcher(player)) {
                player.playSound(Sound {
                    type(org.bukkit.Sound.ENTITY_CHICKEN_EGG)
                    source(Sound.Source.MASTER)
                    volume(10f)
                    pitch(1f)
                }, Sound.Emitter.self())
            }
        }

        player.sendActionBar(
            Component.text("Rekord: $highscore Sprünge", Colors.GOLD).append(Component.text(" | ", Colors.SPACER))
                .append(Component.text("Aktuelle Sprünge: $jumpCount", Colors.GOLD))
        )
    }

    suspend fun announceParkourLoose(player: Player) {
        val uuid = player.uniqueId
        val playerData = DatabaseProvider.getPlayerData(uuid)
        val currentScore = currentPoints.getInt(uuid)
        if (currentScore == NO_CURRENT_POINTS) return

        if (playerData.likesSound) {
            withContext(plugin.entityDispatcher(player)) {
                player.playSound(Sound {
                    type(org.bukkit.Sound.ENTITY_VILLAGER_NO)
                    source(Sound.Source.MASTER)
                    volume(10f)
                    pitch(1f)
                }, Sound.Emitter.self())
            }
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
        val uuid = player.uniqueId
        val playerData = DatabaseProvider.getPlayerData(uuid)
        val currentScore = currentPoints.getInt(uuid)
        if (currentScore == NO_CURRENT_POINTS) return

        val highscore = playerData.highScore

        if (currentScore < highscore) {
            return
        }

        if (playerData.likesSound) {
            withContext(plugin.entityDispatcher(player)) {
                player.playSound(Sound {
                    type(org.bukkit.Sound.ENTITY_PLAYER_LEVELUP)
                    source(Sound.Source.MASTER)
                    volume(10f)
                    pitch(1f)
                }, Sound.Emitter.self())
            }
        }

        player.showTitle(Title {
            title {
                appendText("Herzlichen Glückwunsch!", Colors.GOLD)
            }
            subtitle {
                appendText("Du hast einen neuen Rekord aufgestellt!", Colors.INFO)
            }
            times {
                fadeIn(1.seconds)
                stay(2.seconds)
                fadeOut(1.seconds)
            }
        })

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

        playerData.edit { trys += 1 }
    }

    suspend fun increasePoints(player: Player) {
        val uuid = player.uniqueId
        val playerData = DatabaseProvider.getPlayerData(uuid)

        currentPoints.computeInt(uuid) { _, curPts -> if (curPts == NO_CURRENT_POINTS) 1 else curPts + 1 }
        playerData.edit { points++ }
    }

    private suspend fun updateHighscore(player: Player) {
        val uuid = player.uniqueId
        val currentScore = currentPoints.getInt(uuid)
        if (currentScore == NO_CURRENT_POINTS) return
        val playerData = DatabaseProvider.getPlayerData(uuid)

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

        var minX = min(posOne.blockX, posTwo.blockX)
        var maxX = max(posOne.blockX, posTwo.blockX)
        val minY = min(posOne.blockY, posTwo.blockY)
        val maxY = max(posOne.blockY, posTwo.blockY)
        var minZ = min(posOne.blockZ, posTwo.blockZ)
        var maxZ = max(posOne.blockZ, posTwo.blockZ)

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

        val minX = min(posOne.blockX, posTwo.blockX)
        val maxX = max(posOne.blockX, posTwo.blockX)
        val minY = min(posOne.blockY, posTwo.blockY)
        val maxY = max(posOne.blockY, posTwo.blockY)
        val minZ = min(posOne.blockZ, posTwo.blockZ)
        val maxZ = max(posOne.blockZ, posTwo.blockZ)

        return location.blockX in minX..maxX && location.blockY >= minY && location.blockY <= maxY && location.blockZ >= minZ && location.blockZ <= maxZ
    }

    private suspend fun getValidBlock(previousLocation: Location, player: Player): Block {
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

            if (!isLocationFree(nextLocation)) {
                attempts++
                continue
            }

            val previousWithOffset = previousLocation.clone().add(offsets[0])

            if (!isValidJump(previousWithOffset, nextLocation, player)) {
                attempts++
                continue
            }

            return withContext(plugin.regionDispatcher(nextLocation)) { nextLocation.block }
        }

        val previousWithOffset = previousLocation.clone().add(offsets[0])

        return withContext(plugin.regionDispatcher(previousWithOffset)) { previousWithOffset.block }
    }

    private suspend fun isLocationFree(loc: Location) = withContext(plugin.regionDispatcher(loc)) {
        loc.block.type == Material.AIR
                && loc.clone().add(0.0, 1.0, 0.0).block.type == Material.AIR
                && loc.clone().add(0.0, 2.0, 0.0).block.type == Material.AIR
    }

    private suspend fun isValidJump(
        previousLocation: Location,
        nextLocation: Location,
        player: Player
    ): Boolean = withContext(plugin.regionDispatcher(previousLocation)) {
        val latestPlayerJumps = latestJumps[player.uniqueId] ?: return@withContext true

        for (block in latestPlayerJumps.filterNotNull()) {
            if (block.location.clone().add(0.0, 1.0, 0.0) == nextLocation ||
                block.location.clone().add(0.0, 2.0, 0.0) == nextLocation
            ) {
                return@withContext false
            }
        }

        if (abs(nextLocation.y - previousLocation.y) > 1) {
            return@withContext false
        }

        if (nextLocation == player.location || nextLocation == player.location.clone()
                .add(0.0, 1.0, 0.0)
        ) {
            return@withContext false
        }

        return@withContext true
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
     */
    companion object {
        fun getByName(name: String) = DatabaseProvider.getParkours().find { it.name == name }
        fun isJumping(player: Player) =
            DatabaseProvider.getParkours().any { it.activePlayers.contains(player.uniqueId) }

        fun getParkour(player: Player) =
            DatabaseProvider.getParkours().find { it.activePlayers.contains(player.uniqueId) }
    }
}