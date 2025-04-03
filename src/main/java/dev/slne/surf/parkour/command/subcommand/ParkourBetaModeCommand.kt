package dev.slne.surf.parkour.command.subcommand

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor

import dev.slne.surf.parkour.plugin
import dev.slne.surf.parkour.send
import dev.slne.surf.parkour.util.Permission

import org.bukkit.Bukkit

class ParkourBetaModeCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(Permission.COMMAND_PARKOUR_BETA)

        playerExecutor { player, _ ->
            val betaMode = !plugin.betaMode

            plugin.betaMode = betaMode

            player.send {
                primary("Der Beta-Modus wurde ")
                if (betaMode) {
                    success("aktiviert.")
                } else {
                    error("deaktiviert.")
                }
            }

            this.updateItems(!betaMode, betaMode)
        }
    }

    private fun updateItems(previous: Boolean, now: Boolean) {
        Bukkit.getOnlinePlayers().forEach { player ->
            player.inventory.remove(plugin.getInventoryItem(previous))
            player.inventory.setItem(6, plugin.getInventoryItem(now))
        }
    }
}
