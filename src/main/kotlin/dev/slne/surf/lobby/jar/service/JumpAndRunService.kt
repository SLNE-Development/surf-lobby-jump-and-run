package dev.slne.surf.lobby.jar.service

import com.github.shynixn.mccoroutine.bukkit.launch
import dev.slne.surf.lobby.jar.config.PluginConfig
import dev.slne.surf.lobby.jar.mysql.JumpAndRunPlayerModel
import dev.slne.surf.lobby.jar.mysql.worker.ConnectionWorkers
import dev.slne.surf.lobby.jar.player.JumpAndRunPlayer
import dev.slne.surf.lobby.jar.plugin
import dev.slne.surf.lobby.jar.util.JumpGenerator
import dev.slne.surf.lobby.jar.util.PluginColor
import dev.slne.surf.lobby.jar.util.prefix
import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.security.SecureRandom
import java.time.ZonedDateTime
import java.util.*
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

val random = SecureRandom()

object JumpAndRunService {
    val jumpAndRun: JumpAndRun = PluginConfig.loadJumpAndRun()
    val blocks: Object2ObjectMap<Player, Material> = Object2ObjectOpenHashMap()

    val currentJumpAndRuns = Object2ObjectOpenHashMap<JumpAndRunPlayer, JumpGenerator>()

    // @formatter:off
    val UPDATE_DURATION = 5.minutes

    var latestLeaderboardHighScoreUpdate: ZonedDateTime? = null
        private set

    var latestLeaderboardPointsUpdate: ZonedDateTime? = null
        private set

    val nextLeaderboardHighscoreUpdate = latestLeaderboardHighScoreUpdate?.plus(UPDATE_DURATION.toJavaDuration()) ?: ZonedDateTime.now()
    val nextLeaderboardPointsUpdate = latestLeaderboardPointsUpdate?.plus(UPDATE_DURATION.toJavaDuration()) ?: ZonedDateTime.now()

    var leaderboardPoints = Object2ObjectOpenHashMap<UUID, Int>()
        private set
    var leaderboardHighscores = Object2ObjectOpenHashMap<UUID, Int>()
        private set
    // @formatter:on

    private var runnable: BukkitRunnable? = null

    private suspend fun getHighsccores(count: Int = 10) = ConnectionWorkers.async {
        JumpAndRunPlayerModel.findAll().sortedByDescending { it.highScore }.take(count)
            .associate { it.uuid to it.highScore }
    }

    private suspend fun getPoints(count: Int = 10) = ConnectionWorkers.async {
        JumpAndRunPlayerModel.findAll().sortedByDescending { it.points }.take(count)
            .associate { it.uuid to it.points }
    }

    suspend fun fetchHighscores() {
        leaderboardHighscores = Object2ObjectOpenHashMap(getHighsccores())
        latestLeaderboardHighScoreUpdate = ZonedDateTime.now()
    }

    suspend fun fetchPoints() {
        leaderboardPoints = Object2ObjectOpenHashMap(getPoints())
        latestLeaderboardPointsUpdate = ZonedDateTime.now()
    }

    suspend fun start(jnrPlayer: JumpAndRunPlayer) {
        remove(jnrPlayer)

        val generator = JumpGenerator(jnrPlayer)
        currentJumpAndRuns[jnrPlayer] = generator

        jnrPlayer.incrementTrys()
        generator.generateInitialJumps()

        val highscore = jnrPlayer.highScore
        if (highscore < 1) {
            jnrPlayer.sendMessage(prefix.append(Component.text("Du bist nun im Parkour. Springe so weit wie möglich, um einen Highscore aufzustellen!")))
            return
        }

        jnrPlayer.sendMessage(prefix.append(Component.text("Du bist nun im Parkour. Springe so weit wie möglich, um deinen Highscore von $highscore zu brechen!")))
    }

    fun startTask() {
        runnable = object : BukkitRunnable() {
            override fun run() {
                if (ZonedDateTime.now() >= nextLeaderboardPointsUpdate) {
                    plugin.launch {
                        fetchPoints()
                    }
                }

                if (ZonedDateTime.now() >= nextLeaderboardHighscoreUpdate) {
                    plugin.launch {
                        fetchHighscores()
                    }
                }

                currentJumpAndRuns.forEach { (jnrPlayer, _) ->
                    val player = jnrPlayer.player ?: return@forEach

                    player.sendActionBar(
                        Component.text(
                            jnrPlayer.points,
                            PluginColor.BLUE_MID
                        ).append(Component.text(" Sprünge", PluginColor.DARK_GRAY))
                    )
                }
            }
        }

        runnable?.runTaskTimerAsynchronously(plugin, 0L, 20L)
    }

    fun stopTask() {
        val runnable = runnable ?: return

        try {
            if (!runnable.isCancelled) {
                runnable.cancel()
            }
        } catch (_: IllegalStateException) { // IGNORED
        }
    }

    fun remove(jnrPlayer: JumpAndRunPlayer) {
        val generator = currentJumpAndRuns.remove(jnrPlayer) ?: return
        val player = jnrPlayer.player ?: return

        for (block in generator.latestJumps) {
            if (block == null) continue

            player.sendBlockChange(block.location, Material.AIR.createBlockData())
        }

        jumpAndRun.world?.let { jumpAndRun.spawn?.toLocation(it) }?.let { player.teleportAsync(it) }
    }

    fun onQuit(jnrPlayer: JumpAndRunPlayer) {
        if (isJumping(jnrPlayer)) {
            remove(jnrPlayer)
        }
    }

    fun isJumping(jnrPlayer: JumpAndRunPlayer) = currentJumpAndRuns.containsKey(jnrPlayer)
}
