package net.milocodee.surf.service

import net.milocodee.surf.ActionbarDisplay
import org.bukkit.plugin.Plugin

object ActionbarService {

    private lateinit var display: ActionbarDisplay

    fun init(plugin: Plugin) {
        display = ActionbarDisplay(plugin)
    }

    fun shutdown() {
        if (this::display.isInitialized) {
            display.cleanup()
        }
    }

    fun start(player: org.bukkit.entity.Player, currentJumps: () -> Int, highscore: Int) =
        display.startDisplay(player, currentJumps, highscore)

    fun stop(player: org.bukkit.entity.Player) = display.stopDisplay(player)

    fun updateHighscore(player: org.bukkit.entity.Player, score: Int) =
        display.updateHighscore(player, score)
}
