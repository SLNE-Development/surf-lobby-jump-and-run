package net.milocodee.surf.command

import net.milocodee.surf.listener.ParkourListener
import net.milocodee.surf.utils.MessageUtils.sendInfoMessage
import net.milocodee.surf.utils.MessageUtils.sendSuccessMessage
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ParkourCommand(
    private val parkourListener: ParkourListener
) : CommandExecutor {

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Only players can run this command.")
            return true
        }

        val player = sender

        if (args.isEmpty()) {
            player.sendInfoMessage("Usage: /parkour <start|end>")
            return true
        }

        when (args[0].lowercase()) {
            "start" -> {
                if (parkourListener.isPlayerActive(player)) {
                    player.sendInfoMessage("You are already on a parkour!")
                    return true
                }

                val currentJumps = { 0 }
                val highscore = 0

                parkourListener.onParkourStart(player, currentJumps, highscore)
                player.sendSuccessMessage("Parkour started!")
            }

            "end" -> {
                if (!parkourListener.isPlayerActive(player)) {
                    player.sendInfoMessage("You are not currently on a parkour!")
                    return true
                }

                val finalJumps = 10
                val isNewHighscore = parkourListener.onParkourEnd(player, finalJumps)

                if (isNewHighscore) {
                    player.sendSuccessMessage("Parkour ended! New highscore: $finalJumps")
                } else {
                    player.sendInfoMessage("Parkour ended! You jumped $finalJumps times.")
                }
            }

            else -> {
                player.sendInfoMessage("Usage: /parkour <start|end>")
            }
        }

        return true
    }
}
