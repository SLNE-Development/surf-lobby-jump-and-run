package dev.slne.surf.parkour.command.subcommand

import com.github.shynixn.mccoroutine.bukkit.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.parkour.plugin
import dev.slne.surf.parkour.service.JumpAndRunService
import dev.slne.surf.parkour.util.Colors
import dev.slne.surf.parkour.util.PluginColor
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

class ParkourToggleSoundCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("jumpandrun.command.toggle")

        executesPlayer(PlayerCommandExecutor { player: Player, _: CommandArguments ->
            plugin.launch {
                var sound = JumpAndRunService.querySound(player.uniqueId);

                sound = !sound

                if (sound) {
                    JumpAndRunService.setSound(player, false)
                    player.sendMessage(Colors.PREFIX.append(Component.text("Sounds sind nun für dich deaktiviert.", PluginColor.GOLD)))
                } else {
                    JumpAndRunService.setSound(player, true)
                    player.sendMessage(Colors.PREFIX.append(Component.text("Sounds sind nun für dich aktiviert.", PluginColor.GOLD)))
                }
            }
        })
    }
}
