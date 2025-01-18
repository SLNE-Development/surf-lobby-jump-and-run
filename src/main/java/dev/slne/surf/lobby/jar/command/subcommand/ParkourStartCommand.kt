package dev.slne.surf.lobby.jar.command.subcommand

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.lobby.jar.JumpAndRunService
import dev.slne.surf.lobby.jar.PluginInstance
import org.bukkit.entity.Player

class ParkourStartCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("jumpandrun.command.start")

        executesPlayer(PlayerCommandExecutor { player: Player, _: CommandArguments? ->
            JumpAndRunService.start(player)
        })
    }
}
