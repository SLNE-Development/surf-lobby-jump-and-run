package net.milocodee.surf

import com.github.benmanes.caffeine.cache.Caffeine
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitScheduler
import org.bukkit.scheduler.BukkitTask
import java.util.UUID
import java.util.concurrent.TimeUnit

class ActionbarDisplay(
    private val plugin: Plugin
) {

    private val highscoreCache = Caffeine.newBuilder()
        .expireAfterWrite(5, TimeUnit.MINUTES)
        .build<UUID, Int>()

    private val activeTasks = mutableMapOf<UUID, BukkitTask>()

    fun startDisplay(player: Player, currentJumps: () -> Int, highscore: Int) {
        val uuid = player.uniqueId

        stopDisplay(player)
        highscoreCache.put(uuid, highscore)

        val scheduler: BukkitScheduler = plugin.server.scheduler

        val task = scheduler.runTaskTimerAsynchronously(plugin, Runnable {
            if (!player.isOnline) {
                stopDisplay(player)
                return@Runnable
            }

            val jumps = runCatching { currentJumps() }.getOrDefault(0)
            val cachedHighscore = highscoreCache.getIfPresent(uuid) ?: highscore
            val actionBarComponent = buildActionBarComponent(jumps, cachedHighscore)

            scheduler.runTask(plugin, Runnable {
                if (player.isOnline) {
                    player.sendActionBar(actionBarComponent)
                }
            })
        }, 0L, 10L)

        activeTasks[uuid] = task
    }

    fun stopDisplay(player: Player) {
        val uuid = player.uniqueId

        activeTasks.remove(uuid)?.cancel()
        highscoreCache.invalidate(uuid)

        val scheduler: BukkitScheduler = plugin.server.scheduler
        scheduler.runTask(plugin, Runnable {
            if (player.isOnline) {
                player.sendActionBar(Component.empty())
            }
        })
    }

    fun updateHighscore(player: Player, newHighscore: Int) {
        highscoreCache.put(player.uniqueId, newHighscore)
    }

    private fun buildActionBarComponent(jumps: Int, highscore: Int): Component =
        Component.text()
            .append(
                Component.text("Sprünge: ", NamedTextColor.GRAY)
                    .decoration(TextDecoration.BOLD, false)
            )
            .append(
                Component.text(jumps.toString(), NamedTextColor.GOLD)
                    .decoration(TextDecoration.BOLD, true)
            )
            .append(
                Component.text(" - ", NamedTextColor.DARK_GRAY)
                    .decoration(TextDecoration.BOLD, false)
            )
            .append(
                Component.text("Highscore: ", NamedTextColor.GRAY)
                    .decoration(TextDecoration.BOLD, false)
            )
            .append(
                Component.text(highscore.toString(), NamedTextColor.AQUA)
                    .decoration(TextDecoration.BOLD, true)
            )
            .build()

    fun cleanup() {
        activeTasks.values.forEach { it.cancel() }
        activeTasks.clear()
        highscoreCache.invalidateAll()
    }
}
