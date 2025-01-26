package dev.slne.surf.lobby.jar.command.subcommand.setting

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.LocationArgument
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.lobby.jar.service.JumpAndRunService
import dev.slne.surf.lobby.jar.PluginInstance
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.entity.Player

class ParkourSettingSpawnCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withArguments(LocationArgument("pos"))

        withPermission("jumpandrun.command.setting.setSpawn")

        executesPlayer(PlayerCommandExecutor { player: Player, args: CommandArguments ->
            val pos: Location = args.getUnchecked<Location>("pos") ?: return@PlayerCommandExecutor

            JumpAndRunService.jumpAndRun.spawn = pos.toVector()

            player.sendMessage(PluginInstance.prefix.append(Component.text("Du hast den Spawn erfolgreich neu definiert.")))
        })
    }
}
