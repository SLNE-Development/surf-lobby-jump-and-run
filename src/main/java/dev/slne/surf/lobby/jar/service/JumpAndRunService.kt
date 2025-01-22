package dev.slne.surf.lobby.jar.service

import com.cjcrafter.foliascheduler.TaskImplementation
import com.github.benmanes.caffeine.cache.AsyncLoadingCache
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.shynixn.mccoroutine.bukkit.launch
import dev.hsbrysk.caffeine.CoroutineLoadingCache
import dev.hsbrysk.caffeine.buildCoroutine
import dev.slne.surf.lobby.jar.PluginInstance
import dev.slne.surf.lobby.jar.config.PluginConfig
import dev.slne.surf.lobby.jar.mysql.Database
import dev.slne.surf.lobby.jar.plugin
import dev.slne.surf.lobby.jar.util.PluginColor
import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unimi.dsi.fastutil.objects.ObjectList
import lombok.Getter
import lombok.experimental.Accessors
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import net.kyori.adventure.title.Title
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.security.SecureRandom
import java.time.Duration
import java.util.*
import java.util.function.Consumer
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@Getter
@Accessors(fluent = true)
object JumpAndRunService {
    val jumpAndRun: JumpAndRun = PluginConfig.loadJumpAndRun()
    private val logger = ComponentLogger.logger()

    private val random = SecureRandom()
    private val awaitingHighScores: ObjectList<Player> = ObjectArrayList()
    private val latestJumps: Object2ObjectMap<Player, Array<Block?>> = Object2ObjectOpenHashMap()

    val blocks: Object2ObjectMap<Player, Material> = Object2ObjectOpenHashMap()
    val currentPoints: Object2ObjectMap<Player, Int> = Object2ObjectOpenHashMap()

    private val points: CoroutineLoadingCache<UUID, Int> = Caffeine.newBuilder().buildCoroutine { obj: UUID -> Database.getPoints(obj) }
    private val highScores: CoroutineLoadingCache<UUID, Int> = Caffeine.newBuilder().buildCoroutine { obj: UUID -> Database.getHighScore(obj) }
    private val trys: CoroutineLoadingCache<UUID, Int> = Caffeine.newBuilder().buildCoroutine() { obj: UUID -> Database.getTrys(obj) }
    private val sounds: CoroutineLoadingCache<UUID, Boolean> = Caffeine.newBuilder().buildCoroutine { obj: UUID -> Database.getSound(obj) }

  private val OFFSETS = arrayOf(
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

    private var runnable: TaskImplementation<Void>? = null

    suspend fun start(player: Player) {
        remove(player)

        val jumps = arrayOfNulls<Block>(3)

        latestJumps[player] = jumps
        jumpAndRun.players.add(player)
        currentPoints[player] = 0
        awaitingHighScores.remove(player)

        addTry(player)
        generateInitialJumps(player)

        val highscore: Int? = queryHighScore(player.uniqueId);

        if (highscore == null || highscore < 1) {
            player.sendMessage(PluginInstance.prefix.append(Component.text("Du bist nun im Parkour. Springe so weit wie möglich, um einen Highscore aufzustellen!")))
            return
        }

        player.sendMessage(PluginInstance.prefix.append(Component.text("Du bist nun im Parkour. Springe so weit wie möglich, um deinen Highscore von $highscore zu brechen!")))
    }


    private fun generateInitialJumps(player: Player) {
        val randomLocation = getRandomLocationInRegion(player.world) ?: return

        val start = randomLocation.add(0.0, 1.0, 0.0)
        val block = start.block
        val material = jumpAndRun.materials[random.nextInt(jumpAndRun.materials.size)]

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

    fun startActionbar() {
        runnable = plugin.scheduler.async().runAtFixedRate(Consumer {
            jumpAndRun.players.forEach { player ->
                player.sendActionBar(Component.text(currentPoints[player] ?: -1, PluginColor.BLUE_MID).append(Component.text(" Sprünge", PluginColor.DARK_GRAY)))
            }
        }, 0L, 20L)
    }

    fun stopActionbar() {
        if(runnable == null) {
            return
        }

        val task: TaskImplementation<Void> = runnable ?: return;

        if (!task.isCancelled) {
            task.cancel()
        }
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

    private fun getValidBlock(previousLocation: Location, player: Player): Block {
        val maxAttempts = OFFSETS.size * 2
        var attempts = 0

        while (attempts < maxAttempts) {
            val heightOffset = random.nextInt(3) - 1
            val offset = OFFSETS[random.nextInt(OFFSETS.size)]
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

            val latestPlayerJumps: Array<Block?> = latestJumps[player] ?: return previousLocation.clone().add(
                OFFSETS[0]).block

            val block0: Block = latestPlayerJumps[0] ?: return previousLocation.clone().add(OFFSETS[0]).block
            val block1: Block = latestPlayerJumps[1] ?: return previousLocation.clone().add(OFFSETS[0]).block
            val block2: Block = latestPlayerJumps[2] ?: return previousLocation.clone().add(OFFSETS[0]).block

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

            if (nextLocation == player.location || nextLocation == player.location.clone().add(0.0, 1.0, 0.0)) {
                attempts++
                continue
            }

            return nextLocation.block
        }
        return previousLocation.clone().add(OFFSETS[0]).block
    }


    private fun getRandomLocationInRegion(world: World): Location? {
        val posOne = jumpAndRun.posOne ?: return null
        val posTwo = jumpAndRun.posTwo ?: return null

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
        val posOne = jumpAndRun.posOne ?: return false
        val posTwo = jumpAndRun.posTwo ?: return false

        if (location.world != null && posOne.world != null && posTwo.world != null) {
            if (location.world != posOne.world || location.world != posTwo.world) {
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

    fun getLatestJumps(player: Player): Array<Block?> {
        return latestJumps[player] ?: return arrayOf()
    }

    suspend fun remove(player: Player) {
        if (getLatestJumps(player).isEmpty()) {
            return
        }

        for (block in getLatestJumps(player)) {
          if(block == null) continue

          player.sendBlockChange(block.location, Material.AIR.createBlockData())
        }

        if (awaitingHighScores.contains(player)) {
            setHighScore(player)
        }

        currentPoints.remove(player)
        latestJumps.remove(player)
        jumpAndRun.players.remove(player)

        player.teleportAsync(jumpAndRun.spawn ?: return)
    }

    private suspend fun removeAll() {
        for (player in jumpAndRun.players) {
            remove(player)
        }
    }

    suspend fun saveAll(){
        for (player in points.synchronous().asMap().keys) {
            savePoints(player)
        }

        for (player in sounds.synchronous().asMap().keys) {
            saveSound(player)
        }

        for (player in highScores.synchronous().asMap().keys) {
            saveHighScore(player)
        }

        for (player in trys.synchronous().asMap().keys) {
            saveTrys(player)
        }

        removeAll()
    }

    suspend fun queryTrys(player: UUID): Int {
        return trys.get(player) ?: 0
    }

    suspend fun querySound(player: UUID): Boolean {
        return sounds.get(player) ?: true
    }

    suspend fun queryPoints(player: UUID): Int {
        return points.get(player) ?: 0
    }

    suspend fun queryHighScore(player: UUID): Int {
        return highScores.get(player) ?: 0
    }

    private suspend fun saveSound(player: UUID) {
        val sound: Boolean = querySound(player)

        Database.saveSound(player, sound)
        sounds.synchronous().invalidate(player)
    }

    private suspend fun saveTrys(player: UUID) {
        val tryCount: Int = queryTrys(player)

        Database.saveTrys(player, tryCount)
        trys.synchronous().invalidate(player)
    }

    private suspend fun savePoints(player: UUID) {
        val pointCount: Int = queryPoints(player)

        Database.saveTrys(player, pointCount)
        points.synchronous().invalidate(player)
    }

    private suspend fun saveHighScore(player: UUID) {
        val highScoreCount: Int = queryHighScore(player)

        Database.saveTrys(player, highScoreCount)
        highScores.synchronous().invalidate(player)
    }

    fun setSound(player: Player, value: Boolean) {
        sounds.synchronous().put(player.uniqueId, value)
    }


    suspend fun addPoint(player: Player) {
        val points: Int = currentPoints[player] ?: 1
        val newPoints = points + 1

        JumpAndRunService.points.synchronous().put(player.uniqueId, newPoints)
        currentPoints.compute(player) { _: Player?, curPts: Int? -> if (curPts == null) 1 else curPts + 1 }

        val allowSounds: Boolean = querySound(player.uniqueId)

        if (!allowSounds) {
            player.playSound(
                Sound.sound(
                    org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                    Sound.Source.MASTER,
                    100f,
                    1f
                ), Sound.Emitter.self()
            )
        }
    }

    suspend fun addTry(player: Player) {
        val tryCount: Int = queryTrys(player.uniqueId)
        val newTrys = tryCount + 1

        trys.synchronous().put(player.uniqueId, newTrys)
    }

    suspend fun checkHighScore(player: Player) {
        val currentScore = currentPoints[player]
        val highScore: Int = queryHighScore(player.uniqueId)

        if (currentScore != null && (currentScore > highScore)) {
            awaitingHighScores.add(player)
        }
    }


    private suspend fun setHighScore(player: Player) {
        val currentScore = currentPoints[player]
        val highScore: Int = queryHighScore(player.uniqueId)
        val allowSounds: Boolean = querySound(player.uniqueId)

        if(currentScore == null || currentScore <= highScore) {
            return
        }

        awaitingHighScores.remove(player)
        highScores.synchronous().put(player.uniqueId, currentScore)

        if (allowSounds) {
            player.playSound(Sound.sound(org.bukkit.Sound.ITEM_TOTEM_USE, Sound.Source.MASTER, 100f, 1f), Sound.Emitter.self())
        }

        player.sendMessage(PluginInstance.prefix.append(Component.text("Du hast deinen Highscore gebrochen! Dein neuer Highscore ist ${currentScore}!")));
        player.showTitle(Title.title(Component.text("Rekord!", PluginColor.BLUE_MID), Component.text("Du hast einen neuen persönlichen Rekord aufgestellt.", PluginColor.DARK_GRAY), Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(2), Duration.ofSeconds(1))))
    }


    suspend fun onQuit(player: Player) {
        saveHighScore(player.uniqueId)
        savePoints(player.uniqueId)
        saveTrys(player.uniqueId)

        currentPoints.remove(player)
        awaitingHighScores.remove(player)


        if (isJumping(player)) {
            remove(player)
        }
    }

    fun isJumping(player: Player): Boolean {
        return jumpAndRun.players.contains(player)
    }
}
