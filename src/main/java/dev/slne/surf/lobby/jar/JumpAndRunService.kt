package dev.slne.surf.lobby.jar

import com.github.benmanes.caffeine.cache.AsyncLoadingCache
import com.github.benmanes.caffeine.cache.CacheLoader
import com.github.benmanes.caffeine.cache.Caffeine
import dev.slne.surf.lobby.jar.config.PluginConfig
import dev.slne.surf.lobby.jar.mysql.Database
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
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import java.security.SecureRandom
import java.time.Duration
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@Getter
@Accessors(fluent = true)
object JumpAndRunService {
    val jumpAndRun: JumpAndRun = PluginConfig.loadJumpAndRun()
    private val random = SecureRandom()
    private val awaitingHighScores: ObjectList<Player> = ObjectArrayList()
    private val latestJumps: Object2ObjectMap<Player, Array<Block>> = Object2ObjectOpenHashMap()
    val blocks: Object2ObjectMap<Player, Material> = Object2ObjectOpenHashMap()
    val currentPoints: Object2ObjectMap<Player, Int> = Object2ObjectOpenHashMap()

    private val points: AsyncLoadingCache<UUID, Int?> = Caffeine.newBuilder()
        .buildAsync(CacheLoader { obj: UUID -> Database.getPoints(obj) })

    private val highScores: AsyncLoadingCache<UUID, Int?> = Caffeine.newBuilder()
        .buildAsync { obj: UUID -> Database.getHighScore(obj) }

    private val trys: AsyncLoadingCache<UUID, Int?> = Caffeine.newBuilder()
        .buildAsync { obj: UUID -> Database.getTrys(obj) }

    private val sounds: AsyncLoadingCache<UUID, Boolean?> = Caffeine.newBuilder()
        .buildAsync { obj: UUID -> Database.getSound(obj) }

    private var runnable: BukkitRunnable? = null

    fun start(player: Player) {
        this.remove(player)

        val jumps = arrayOfNulls<Block>(3)

        latestJumps[player] = jumps
        jumpAndRun.getPlayers().add(player)
        currentPoints[player] = 0
        awaitingHighScores.remove(player)

        this.addTry(player)
        this.generateInitialJumps(player)

        queryHighScore(player.uniqueId).thenAccept { highScore: Int? ->
            if (highScore == null) {
                player.sendMessage(
                    PluginInstance.prefix()
                        .append(Component.text("Du bist nun im Parkour. Springe so weit wie möglich, um einen Highscore aufzustellen!"))
                )
                return@thenAccept
            }
            player.sendMessage(
                PluginInstance.prefix().append(
                    Component.text(
                        String.format(
                            "Du bist nun im Parkour. Springe so weit wie möglich, versuche deinen Highscore von %s zu brechen!",
                            highScore
                        )
                    )
                )
            )
        }.exceptionally { throwable: Throwable? ->
            logger.error(
                "An error occurred starting jump and run.",
                throwable
            )
            null
        }
    }


    private fun generateInitialJumps(player: Player) {
        val randomLocation = this.getRandomLocationInRegion(player.world) ?: return

        val start = randomLocation.add(0.0, 1.0, 0.0)
        val block = start.block
        val material = jumpAndRun.getMaterials()[random.nextInt(jumpAndRun.getMaterials().size)]

        player.sendBlockChange(block.location, material.createBlockData())
        latestJumps[player]!![0] = block

        val next = this.getValidBlock(start, player)

        player.sendBlockChange(next.location, Material.SEA_LANTERN.createBlockData())
        latestJumps[player]!![1] = next

        val next2 = this.getValidBlock(next.location, player)

        player.sendBlockChange(next2.location, material.createBlockData())
        latestJumps[player]!![2] = next2

        player.teleportAsync(block.location.add(0.5, 1.0, 0.5))
        blocks[player] = material
    }

    fun startActionbar() {
        runnable = object : BukkitRunnable() {
            override fun run() {
                jumpAndRun.getPlayers().forEach(Consumer { player: Player ->
                    player.sendActionBar(
                        Component.text(
                            currentPoints[player]!!, PluginColor.BLUE_MID
                        )
                            .append(Component.text(" Spr\u00FCnge", PluginColor.DARK_GRAY))
                    )
                })
            }
        }
        runnable.runTaskTimerAsynchronously(PluginInstance.Companion.instance(), 0L, 20L)
    }

    fun stopActionbar() {
        if (runnable != null && !runnable!!.isCancelled) {
            runnable!!.cancel()
        }
    }

    fun generate(player: Player) {
        val jumps = latestJumps[player]!!
        val material = blocks[player]!!

        if (jumps[0] != null) {
            player.sendBlockChange(jumps[0]!!.location, Material.AIR.createBlockData())
        }

        jumps[0] = jumps[1]
        jumps[1] = jumps[2]

        player.sendBlockChange(jumps[1]!!.location, Material.SEA_LANTERN.createBlockData())

        val nextJump = getValidBlock(jumps[1]!!.location, player)
        player.sendBlockChange(nextJump.location, material.createBlockData())
        jumps[2] = nextJump
    }

    private fun getValidBlock(previousLocation: Location, player: Player): Block {
        val maxAttempts = OFFSETS.size * 2
        var attempts = 0

        while (attempts < maxAttempts) {
            val heightOffset = random.nextInt(3) - 1
            val offset = OFFSETS[random.nextInt(OFFSETS.size)]
            val nextLocation =
                previousLocation.clone().add(offset).add(0.0, heightOffset.toDouble(), 0.0)

            if (!this.isInRegion(nextLocation)) {
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

            /* Above the Jump */
            if (latestJumps[player]!![0] != null && latestJumps[player]!![0]!!.location.clone()
                    .add(0.0, 1.0, 0.0) == nextLocation
            ) {
                attempts++
                continue
            }
            if (latestJumps[player]!![1] != null && latestJumps[player]!![1]!!.location.clone()
                    .add(0.0, 1.0, 0.0) == nextLocation
            ) {
                attempts++
                continue
            }
            if (latestJumps[player]!![2] != null && latestJumps[player]!![2]!!.location.clone()
                    .add(0.0, 1.0, 0.0) == nextLocation
            ) {
                attempts++
                continue
            }

            /* 2 Blocks above the Jump */
            if (latestJumps[player]!![0] != null && latestJumps[player]!![0]!!.location.clone()
                    .add(0.0, 2.0, 0.0) == nextLocation
            ) {
                attempts++
                continue
            }
            if (latestJumps[player]!![1] != null && latestJumps[player]!![1]!!.location.clone()
                    .add(0.0, 2.0, 0.0) == nextLocation
            ) {
                attempts++
                continue
            }
            if (latestJumps[player]!![2] != null && latestJumps[player]!![2]!!.location.clone()
                    .add(0.0, 2.0, 0.0) == nextLocation
            ) {
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
        return previousLocation.clone().add(OFFSETS[0]).block
    }


    private fun getRandomLocationInRegion(world: World): Location? {
        val posOne = jumpAndRun.getPosOne()
        val posTwo = jumpAndRun.getPosTwo()

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


    fun isInRegion(location: Location): Boolean {
        val posOne = jumpAndRun.getPosOne()
        val posTwo = jumpAndRun.getPosTwo()

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

        return location.blockX >= minX && location.blockX <= maxX && location.blockY >= minY && location.blockY <= maxY && location.blockZ >= minZ && location.blockZ <= maxZ
    }

    fun getLatestJumps(player: Player): Array<Block> {
        return latestJumps[player] ?: return arrayOf()
    }

    fun remove(player: Player) {
        if (this.getLatestJumps(player).isEmpty()) {
            return
        }

        for (block in getLatestJumps(player)) {
          player.sendBlockChange(block.location, Material.AIR.createBlockData())
        }

        if (awaitingHighScores.contains(player)) {
            this.setHighScore(player)
        }

        currentPoints.remove(player)
        latestJumps.remove(player)
        jumpAndRun.getPlayers().remove(player)

        player.teleportAsync(jumpAndRun.getSpawn())
    }

    fun removeAll() {
        for (player in jumpAndRun.getPlayers()) {
            this.remove(player)
        }
    }

    fun saveAll(): CompletableFuture<Void?> {
        val futures: ObjectList<CompletableFuture<Void>> = ObjectArrayList()

        for (player in points.synchronous().asMap().keys) {
            val future = savePoints(player)
            futures.add(future)
        }

        for (player in sounds.synchronous().asMap().keys) {
            val future = saveSound(player)
            futures.add(future)
        }

        for (player in highScores.synchronous().asMap().keys) {
            val future = saveHighScore(player)
            futures.add(future)
        }

        for (player in trys.synchronous().asMap().keys) {
            val future = saveTrys(player)
            futures.add(future)
        }

        val allSaves =
            CompletableFuture.allOf(*futures.toArray<CompletableFuture<*>> { _Dummy_.__Array__() })

        return allSaves.thenRun { this.removeAll() }.exceptionally { throwable: Throwable? ->
            logger.error(
                "An error occurred while saving all data",
                throwable
            )
            null
        }
    }

    fun queryTrys(player: UUID): CompletableFuture<Int?> {
        return trys[player]
    }

    fun querySound(player: UUID): CompletableFuture<Boolean?> {
        return sounds[player]
    }

    fun queryPoints(player: UUID): CompletableFuture<Int?> {
        return points[player]
    }

    fun queryHighScore(player: UUID): CompletableFuture<Int?> {
        return highScores[player]
    }

    fun saveSound(player: UUID): CompletableFuture<Void> {
        return querySound(player).thenCompose { sound: Boolean? ->
            CompletableFuture.runAsync {
                Database.saveSound(
                    player,
                    sound
                )
            }.thenRun { sounds.synchronous().invalidate(player) }
        }
    }

    fun saveTrys(player: UUID): CompletableFuture<Void> {
        return queryTrys(player).thenCompose { points: Int? ->
            if (points == null) {
                return@thenCompose CompletableFuture.completedFuture<Void?>(
                    null
                )
            }
            CompletableFuture.runAsync { Database.saveTrys(player, points) }
                .thenRun {
                    trys.synchronous()
                        .invalidate(player)
                }
        }
    }

    fun savePoints(player: UUID): CompletableFuture<Void> {
        return queryPoints(player).thenCompose { points: Int? ->
            if (points == null) {
                return@thenCompose CompletableFuture.completedFuture<Void?>(
                    null
                )
            }
            CompletableFuture.runAsync { Database.savePoints(player, points) }
                .thenRun {
                    this.points.synchronous()
                        .invalidate(player)
                }
        }
    }

    fun saveHighScore(player: UUID): CompletableFuture<Void> {
        return queryHighScore(player).thenCompose { highScore: Int? ->
            if (highScore == null) {
                return@thenCompose CompletableFuture.completedFuture<Void?>(
                    null
                )
            }
            CompletableFuture.runAsync {
                Database.saveHighScore(
                    player,
                    highScore
                )
            }.thenRun { highScores.synchronous().invalidate(player) }
        }
    }

    fun setSound(player: Player, value: Boolean?) {
        sounds.synchronous().put(player.uniqueId, value)
    }


    fun addPoint(player: Player) {
        points[player.uniqueId].thenAccept { points: Int? ->
            val newPoints = if (points == null) 1 else points + 1
            this.points.synchronous().put(player.uniqueId, newPoints)
        }

        currentPoints.compute(player) { p: Player?, curPts: Int? -> if (curPts == null) 1 else curPts + 1 }

        querySound(player.uniqueId).thenAccept { sound: Boolean? ->
            if (!sound!!) {
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
    }

    fun addTry(player: Player) {
        queryTrys(player.uniqueId).thenAccept { trys: Int? ->
            val newTrys = if (trys == null) 1 else trys + 1
            this.trys.synchronous().put(player.uniqueId, newTrys)
        }
    }

    fun checkHighScore(player: Player) {
        val currentScore = currentPoints[player]

        queryHighScore(player.uniqueId).thenAccept { highScore: Int? ->
            if (currentScore != null && (highScore == null || currentScore > highScore)) {
                awaitingHighScores.add(player)
            }
        }
    }


    fun setHighScore(player: Player) {
        val currentScore = currentPoints[player]

        queryHighScore(player.uniqueId).thenAccept { highScore: Int? ->
            if (currentScore != null && (highScore == null || currentScore > highScore)) {
                awaitingHighScores.remove(player)
                highScores.synchronous().put(player.uniqueId, currentScore)

                player.sendMessage(
                    PluginInstance.prefix().append(
                        Component.text(
                            String.format(
                                "Du hast deinen Highscore gebrochen! Dein neuer Highscore ist %s!",
                                currentScore
                            )
                        )
                    )
                )

                querySound(player.uniqueId)
                    .thenAccept { sound: Boolean? ->
                        if (!sound!!) {
                            player.playSound(
                                Sound.sound(
                                    org.bukkit.Sound.ITEM_TOTEM_USE,
                                    Sound.Source.MASTER,
                                    100f,
                                    1f
                                ), Sound.Emitter.self()
                            )
                        }
                    }

                player.showTitle(
                    Title.title(
                        Component.text("Rekord!", PluginColor.BLUE_MID),
                        Component.text(
                            "Du hast einen neuen persönlichen Rekord aufgestellt.",
                            PluginColor.DARK_GRAY
                        ),
                        Title.Times.times(
                            Duration.ofSeconds(1),
                            Duration.ofSeconds(2),
                            Duration.ofSeconds(1)
                        )
                    )
                )
            }
        }
    }


    fun onQuit(player: Player) {
        this.saveHighScore(player.uniqueId)
        this.savePoints(player.uniqueId)
        this.saveTrys(player.uniqueId)

        currentPoints.remove(player)
        awaitingHighScores.remove(player)


        if (this.isJumping(player)) {
            this.remove(player)
        }
    }

    fun isJumping(player: Player?): Boolean {
        return jumpAndRun.getPlayers().contains(player)
    }

    companion object {
        private val logger = ComponentLogger.logger()

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
    }
}
