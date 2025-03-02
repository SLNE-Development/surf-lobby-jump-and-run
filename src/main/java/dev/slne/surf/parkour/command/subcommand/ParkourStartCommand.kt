package dev.slne.surf.parkour.command.subcommand

import com.github.shynixn.mccoroutine.bukkit.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.parkour.plugin
import dev.slne.surf.parkour.service.JumpAndRunService
import org.bukkit.entity.Player

class ParkourStartCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("jumpandrun.command.start")

        executesPlayer(PlayerCommandExecutor { player: Player, _: CommandArguments? ->
            plugin.launch {
                JumpAndRunService.start(player)
            }
        })
    }
}
