package dev.slne.surf.lobby.jar.command.subcommand

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.lobby.jar.JumpAndRunService
import dev.slne.surf.lobby.jar.PluginInstance
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player

class ParkourListCommand(commandName: String) : CommandAPICommand(commandName) {
    private val provider: JumpAndRunService? =
        PluginInstance.Companion.instance().jumpAndRunProvider()

    init {
        withPermission("jumpandrun.command.list")

        executesPlayer(PlayerCommandExecutor { player: Player, args: CommandArguments? ->
            val playerCount = provider!!.jumpAndRun().players.size
            if (playerCount == 0) {
                player.sendMessage(
                    PluginInstance.prefix()
                        .append(
                            Component.text("Aktuell sind ", NamedTextColor.GRAY)
                                .append(Component.text("keine Spieler", NamedTextColor.YELLOW))
                                .append(Component.text(" im Jump And Run.", NamedTextColor.WHITE))
                        )
                )
                return@executesPlayer
            }

            val header: Component = Component.text("Aktuell sind ", NamedTextColor.GRAY)
                .append(Component.text("$playerCount Spieler", NamedTextColor.YELLOW))
                .append(Component.text(" im Jump And Run: ", NamedTextColor.WHITE))

            var playerList: Component = Component.empty()
            var current = 0

            for (target in provider.jumpAndRun().players) {
                current++

                var playerComponent: Component = Component.text(target.name, NamedTextColor.WHITE)
                    .append(Component.text(" (", NamedTextColor.GRAY))
                    .append(Component.text(provider.currentPoints()[target], NamedTextColor.YELLOW))
                    .append(Component.text(")", NamedTextColor.GRAY))

                if (current < playerCount) {
                    playerComponent =
                        playerComponent.append(Component.text(", ", NamedTextColor.GRAY))
                }

                playerList = playerList.append(playerComponent)
            }
            player.sendMessage(PluginInstance.prefix().append(header.append(playerList)))
        })
    }
}
