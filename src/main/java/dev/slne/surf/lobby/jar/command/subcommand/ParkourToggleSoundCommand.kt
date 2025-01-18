package dev.slne.surf.lobby.jar.command.subcommand

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.lobby.jar.JumpAndRunService
import dev.slne.surf.lobby.jar.PluginInstance
import dev.slne.surf.lobby.jar.util.PluginColor
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

class ParkourToggleSoundCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("jumpandrun.command.toggle")

        executesPlayer(PlayerCommandExecutor { player: Player, _: CommandArguments ->
            JumpAndRunService.querySound(player.uniqueId).thenAccept { sound: Boolean? ->
                var setting = sound

                if (setting == null) {
                    setting = true
                }

                setting = !setting

                if (setting) {
                    JumpAndRunService.setSound(player, false)
                    player.sendMessage(PluginInstance.prefix.append(Component.text("Sounds sind nun für dich aktiviert.", PluginColor.GOLD)))
                } else {
                    JumpAndRunService.setSound(player, true)
                    player.sendMessage(PluginInstance.prefix.append(Component.text("Sounds sind nun für dich deaktiviert.", PluginColor.GOLD)))
                }
            }
        })
    }
}
