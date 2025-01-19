package dev.slne.surf.lobby.jar.command.subcommand

import com.github.shynixn.mccoroutine.bukkit.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.PlayerArgument
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.lobby.jar.plugin
import dev.slne.surf.lobby.jar.service.JumpAndRunService
import dev.slne.surf.lobby.jar.util.PluginColor
import dev.slne.surf.lobby.jar.util.toJnrPlayer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.entity.Player

class ParkourStatsCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("jumpandrun.command.stats")

        withOptionalArguments(PlayerArgument("target"))

        executesPlayer(PlayerCommandExecutor { player: Player, args: CommandArguments ->
            plugin.launch {
                val target = args.getOrDefaultUnchecked("target", player)
                val targetJnrPlayer = target.toJnrPlayer()

                val points = targetJnrPlayer.points
                val highScore = targetJnrPlayer.highScore
                val trys = targetJnrPlayer.trys
                val currentPoints = 0 // TODO: Fixme and reimplement

                player.sendMessage(
                    createStatisticMessage(
                        points.toString(),
                        highScore.toString(),
                        if (JumpAndRunService.isJumping(targetJnrPlayer)) currentPoints.toString() else "Kein laufender Parkour",
                        trys.toString()
                    )
                )
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
