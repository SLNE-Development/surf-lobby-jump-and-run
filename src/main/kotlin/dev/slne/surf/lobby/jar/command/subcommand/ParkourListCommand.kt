package dev.slne.surf.lobby.jar.command.subcommand

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.lobby.jar.service.JumpAndRunService
import dev.slne.surf.lobby.jar.util.prefix
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player

class ParkourListCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("jumpandrun.command.list")

        executesPlayer(PlayerCommandExecutor { player: Player, _: CommandArguments? ->
            val currentJumpAndRuns = JumpAndRunService.currentJumpAndRuns
            val playerCount = currentJumpAndRuns.size

            if (playerCount == 0) {
                player.sendMessage(
                    prefix.append(
                        Component.text("Aktuell sind ", NamedTextColor.GRAY)
                            .append(Component.text("keine Spieler", NamedTextColor.YELLOW))
                            .append(Component.text(" im Jump And Run.", NamedTextColor.WHITE))
                    )
                )

                return@PlayerCommandExecutor
            }

            val header: Component = Component.text("Aktuell sind ", NamedTextColor.GRAY)
                .append(Component.text("$playerCount Spieler", NamedTextColor.YELLOW))
                .append(Component.text(" im Jump And Run: ", NamedTextColor.WHITE))

            val components = currentJumpAndRuns.map { it.key }.map {
                val playerName = it.player?.name ?: "Unbekannt"
                val points = it.points

                Component.text(playerName, NamedTextColor.WHITE)
                    .append(Component.text(" (", NamedTextColor.GRAY))
                    .append(Component.text(points, NamedTextColor.YELLOW))
                    .append(Component.text(")", NamedTextColor.GRAY))
            }

            val playerList = Component.text()

            components.forEachIndexed { index, component ->
                playerList.append(component)

                if (index < components.size - 1) {
                    playerList.append(Component.text(", ", NamedTextColor.GRAY))
                }
            }

            player.sendMessage(prefix.append(header.append(playerList)))
        })
    }
}
