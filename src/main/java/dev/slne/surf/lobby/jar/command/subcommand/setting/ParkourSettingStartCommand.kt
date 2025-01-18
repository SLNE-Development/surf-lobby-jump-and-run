package dev.slne.surf.lobby.jar.command.subcommand.setting

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.LocationArgument
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.lobby.jar.PluginInstance
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.entity.Player

class ParkourSettingStartCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("jumpandrun.command.setting.setStart")

        withArguments(LocationArgument("pos"))

        executesPlayer(PlayerCommandExecutor { player: Player, args: CommandArguments ->
            val pos = args.getUnchecked<Location>("pos")
            PluginInstance.instance().jumpAndRunProvider().jumpAndRun().setStart(pos)
            player.sendMessage(
                PluginInstance.prefix()
                    .append(Component.text("Du hast den Start erfolgreich neu definiert."))
            )
        })
    }
}
