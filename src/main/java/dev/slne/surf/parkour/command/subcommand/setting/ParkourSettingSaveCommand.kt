package dev.slne.surf.parkour.command.subcommand.setting

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.parkour.config.PluginConfig
import dev.slne.surf.parkour.service.JumpAndRunService
import dev.slne.surf.parkour.util.Colors
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

class ParkourSettingSaveCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("jumpandrun.command.setting.save")

        executesPlayer(PlayerCommandExecutor { player: Player, _: CommandArguments? ->
            PluginConfig.save(JumpAndRunService.jumpAndRun)

            player.sendMessage(Colors.PREFIX.append(Component.text("Du hast erfolgreich die Einstellungen gespeichert.")))
        })
    }
}
