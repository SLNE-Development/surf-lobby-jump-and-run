package net.milocodee.surf.listener

import net.milocodee.surf.ActionbarDisplay
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class ParkourListener(
    private val actionbarDisplay: ActionbarDisplay
) : Listener {

    private val activeParkours: MutableMap<Player, ParkourSession> = mutableMapOf()

    fun onParkourStart(player: Player, getCurrentJumps: () -> Int, highscore: Int) {
        val session = ParkourSession(getCurrentJumps, highscore)
        activeParkours[player] = session

        actionbarDisplay.startDisplay(player, getCurrentJumps, highscore)
    }

    fun onParkourEnd(player: Player, finalJumps: Int): Boolean {
        val session = activeParkours.remove(player) ?: return false

        val isNewHighscore = finalJumps < session.highscore || session.highscore == 0

        if (isNewHighscore) {
            actionbarDisplay.updateHighscore(player, finalJumps)
        }

        actionbarDisplay.stopDisplay(player)

        return isNewHighscore
    }

    fun onParkourLeave(player: Player) {
        activeParkours.remove(player)
        actionbarDisplay.stopDisplay(player)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player
        if (activeParkours.containsKey(player)) {
            onParkourLeave(player)
        }
    }

    data class ParkourSession(
        val getCurrentJumps: () -> Int,
        val highscore: Int
    )
}
