package net.milocodee.surf.listener

import net.milocodee.surf.service.ActionbarService
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import java.util.UUID

class ParkourListener(
) : Listener {

    private val activeParkours = mutableMapOf<UUID, ParkourSession>()

    fun onParkourStart(player: Player, getCurrentJumps: () -> Int, highscore: Int) {
        val uuid = player.uniqueId
        val session = ParkourSession(getCurrentJumps, highscore)
        activeParkours[uuid] = session

        ActionbarService.start(player, getCurrentJumps, highscore)
    }

    fun onParkourEnd(player: Player, finalJumps: Int): Boolean {
        val uuid = player.uniqueId
        val session = activeParkours.remove(uuid) ?: return false

        val isNewHighscore = finalJumps < session.highscore || session.highscore == 0
        if (isNewHighscore) {
            ActionbarService.updateHighscore(player, finalJumps)
        }

        ActionbarService.stop(player)
        return isNewHighscore
    }

    fun onParkourLeave(player: Player) {
        activeParkours.remove(player.uniqueId)
        ActionbarService.stop(player)
    }

    fun isPlayerActive(player: Player): Boolean {
        return activeParkours.containsKey(player.uniqueId)
    }


    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        onParkourLeave(event.player)
    }

    private data class ParkourSession(
        val getCurrentJumps: () -> Int,
        val highscore: Int
    )
}
