package dev.slne.surf.lobby.jar.command.subcommand.setting

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.LocationArgument
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.lobby.jar.service.JumpAndRunService
import dev.slne.surf.lobby.jar.PluginInstance
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

class ParkourSettingAreaCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("jumpandrun.command.setting.area")
        withArguments(LocationArgument("pos1"), LocationArgument("pos2"))

        executesPlayer(PlayerCommandExecutor { player: Player, args: CommandArguments ->
            JumpAndRunService.jumpAndRun.posOne = args.getUnchecked("pos1")
            JumpAndRunService.jumpAndRun.posTwo = args.getUnchecked("pos2")

            player.sendMessage(PluginInstance.prefix.append(Component.text("Du hast die Arena erfolgreich neu definiert.")))
        })
    }
}
