package dev.slne.surf.lobby.jar.command.subcommand.setting

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.lobby.jar.config.PluginConfig.save
import dev.slne.surf.lobby.jar.service.JumpAndRunService
import dev.slne.surf.lobby.jar.util.prefix
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

class ParkourSettingSaveCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("jumpandrun.command.setting.save")

        executesPlayer(PlayerCommandExecutor { player: Player, _: CommandArguments? ->
            save(JumpAndRunService.jumpAndRun)

            player.sendMessage(prefix.append(Component.text("Du hast erfolgreich die Einstellungen gespeichert.")))
        })
    }
}
