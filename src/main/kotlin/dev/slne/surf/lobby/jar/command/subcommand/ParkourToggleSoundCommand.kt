package dev.slne.surf.lobby.jar.command.subcommand

import com.github.shynixn.mccoroutine.bukkit.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.lobby.jar.plugin
import dev.slne.surf.lobby.jar.util.PluginColor
import dev.slne.surf.lobby.jar.util.prefix
import dev.slne.surf.lobby.jar.util.toJnrPlayer
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

class ParkourToggleSoundCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("jumpandrun.command.toggle")

        executesPlayer(PlayerCommandExecutor { player: Player, _: CommandArguments ->
            plugin.launch {
                val jnrPlayer = player.toJnrPlayer()

                val newSound = !jnrPlayer.sound
                jnrPlayer.setSound(newSound)

                if (newSound) {
                    player.sendMessage(
                        prefix.append(
                            Component.text(
                                "Sounds sind nun für dich aktiviert.",
                                PluginColor.GOLD
                            )
                        )
                    )
                } else {
                    player.sendMessage(
                        prefix.append(
                            Component.text(
                                "Sounds sind nun für dich deaktiviert.",
                                PluginColor.GOLD
                            )
                        )
                    )
                }
            }
        })
    }
}
