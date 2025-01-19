package dev.slne.surf.lobby.jar.command.subcommand

import com.github.shynixn.mccoroutine.bukkit.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.lobby.jar.plugin
import dev.slne.surf.lobby.jar.service.JumpAndRunService
import dev.slne.surf.lobby.jar.util.toJnrPlayer
import org.bukkit.entity.Player

class ParkourStartCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("jumpandrun.command.start")

        executesPlayer(PlayerCommandExecutor { player: Player, _: CommandArguments? ->
            plugin.launch {
                JumpAndRunService.start(player.toJnrPlayer())
            }
        })
    }
}
