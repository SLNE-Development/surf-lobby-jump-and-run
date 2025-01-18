package dev.slne.surf.lobby.jar.command.subcommand

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.PlayerArgument
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.lobby.jar.JumpAndRunService
import dev.slne.surf.lobby.jar.util.PluginColor
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.entity.Player

class ParkourStatsCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("jumpandrun.command.stats")

        withOptionalArguments(PlayerArgument("target"))

        executesPlayer(PlayerCommandExecutor { player: Player, args: CommandArguments ->
            val target = args.getOrDefaultUnchecked("target", player)

            JumpAndRunService.queryHighScore(target.uniqueId).thenAccept { highScore: Int? ->
                JumpAndRunService.queryPoints(target.uniqueId).thenAccept { points: Int? ->
                    JumpAndRunService.queryTrys(target.uniqueId).thenAccept { trys: Int? ->
                        if (points == null || highScore == null) {
                            player.sendMessage(Component.text("Aktuell sind keine Statistiken verfügbar... (Rejoin?)", PluginColor.RED))
                            return@thenAccept
                        }

                        val currentPoints = JumpAndRunService.currentPoints[player] ?: 0

                        player.sendMessage(createStatisticMessage(points.toString(), highScore.toString(), if (JumpAndRunService.isJumping(target)) currentPoints.toString() else "Kein laufender Parkour", trys.toString()))
                    }
                }
            }
        })
    }

    companion object {
        fun createStatisticMessage(
            points: String,
            highScore: String,
            current: String,
            trys: String
        ): Component {
            return Component.text(">> ", PluginColor.DARK_GRAY)
                .append(Component.text("Parkour ", PluginColor.BLUE))
                .append(Component.text("| ", PluginColor.DARK_GRAY))
                .append(Component.text("-------------", PluginColor.LIGHT_GRAY))
                .append(
                    Component.text("STATISTIK", PluginColor.BLUE_LIGHT)
                        .decorate(TextDecoration.BOLD)
                )
                .append(Component.text("-------------", PluginColor.LIGHT_GRAY))
                .append(Component.newline())
                .append(Component.text(">> ", PluginColor.DARK_GRAY))
                .append(Component.text("Parkour ", PluginColor.BLUE))
                .append(Component.text("|", PluginColor.DARK_GRAY))
                .append(Component.newline())
                .append(Component.text(">> ", PluginColor.DARK_GRAY))
                .append(Component.text("Parkour ", PluginColor.BLUE))
                .append(Component.text("| ", PluginColor.DARK_GRAY))
                .append(Component.text("Seit Aufzeichnung:", PluginColor.DARK_GRAY))
                .append(Component.newline())
                .append(Component.text(">> ", PluginColor.DARK_GRAY))
                .append(Component.text("Parkour ", PluginColor.BLUE))
                .append(Component.text("|", PluginColor.DARK_GRAY))
                .append(Component.newline())
                .append(Component.text(">> ", PluginColor.DARK_GRAY))
                .append(Component.text("Parkour ", PluginColor.BLUE))
                .append(Component.text("|    ", PluginColor.DARK_GRAY))
                .append(Component.text("Sprünge: ", PluginColor.BLUE_MID))
                .append(Component.text(points, PluginColor.GOLD))
                .append(Component.newline())
                .append(Component.text(">> ", PluginColor.DARK_GRAY))
                .append(Component.text("Parkour ", PluginColor.BLUE))
                .append(Component.text("|    ", PluginColor.DARK_GRAY))
                .append(Component.text("Rekord: ", PluginColor.BLUE_MID))
                .append(Component.text(highScore, PluginColor.GOLD))
                .append(Component.newline())
                .append(Component.text(">> ", PluginColor.DARK_GRAY))
                .append(Component.text("Parkour ", PluginColor.BLUE))
                .append(Component.text("|    ", PluginColor.DARK_GRAY))
                .append(Component.text("Versuche: ", PluginColor.BLUE_MID))
                .append(Component.text(trys, PluginColor.GOLD))
                .append(Component.newline())
                .append(Component.text(">> ", PluginColor.DARK_GRAY))
                .append(Component.text("Parkour ", PluginColor.BLUE))
                .append(Component.text("|", PluginColor.DARK_GRAY))
                .append(Component.newline())
                .append(Component.text(">> ", PluginColor.DARK_GRAY))
                .append(Component.text("Parkour ", PluginColor.BLUE))
                .append(Component.text("| Laufender Parkour:", PluginColor.DARK_GRAY))
                .append(Component.newline())
                .append(Component.text(">> ", PluginColor.DARK_GRAY))
                .append(Component.text("Parkour ", PluginColor.BLUE))
                .append(Component.text("|", PluginColor.DARK_GRAY))
                .append(Component.newline())
                .append(Component.text(">> ", PluginColor.DARK_GRAY))
                .append(Component.text("Parkour ", PluginColor.BLUE))
                .append(Component.text("|    ", PluginColor.DARK_GRAY))
                .append(Component.text("Sprünge: ", PluginColor.BLUE_MID))
                .append(Component.text(current, PluginColor.GOLD))
                .append(Component.newline())
                .append(Component.text(">> ", PluginColor.DARK_GRAY))
                .append(Component.text("Parkour ", PluginColor.BLUE))
                .append(Component.text("|", PluginColor.DARK_GRAY))
                .append(Component.newline())
                .append(Component.text(">> ", PluginColor.DARK_GRAY))
                .append(Component.text("Parkour ", PluginColor.BLUE))
                .append(Component.text("| ", PluginColor.DARK_GRAY))
                .append(
                    Component.text(
                        "-----------------------------------",
                        PluginColor.LIGHT_GRAY
                    )
                )
        }
    }
}
