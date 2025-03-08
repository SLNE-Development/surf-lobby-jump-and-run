package dev.slne.surf.parkour.command.subcommand

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.PlayerArgument
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.parkour.database.DatabaseProvider
import dev.slne.surf.parkour.parkour.Parkour
import dev.slne.surf.parkour.plugin
import dev.slne.surf.parkour.util.Colors
import dev.slne.surf.parkour.util.Permission
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.entity.Player

class ParkourStatsCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(Permission.COMMAND_PARKOUR_STATISTIC)
        withOptionalArguments(PlayerArgument("target"))
        executesPlayer(PlayerCommandExecutor { player: Player, args: CommandArguments ->
            val target = args.getOrDefaultUnchecked("target", player)

            plugin.launch {
                val playerData = DatabaseProvider.getPlayerData(target.uniqueId)


                val parkour = Parkour.getParkour(target)

                if(parkour == null) {
                    player.sendMessage(createStatisticMessage(
                        playerData.points.toString(),
                        playerData.highScore.toString(),
                        "Du spielst aktuell keinen Parkour",
                        playerData.trys.toString()
                        )
                    )
                } else {
                    player.sendMessage(createStatisticMessage(
                        playerData.points.toString(),
                        playerData.highScore.toString(),
                        parkour.currentPoints.getInt(target.uniqueId).toString(),
                        playerData.trys.toString()
                    )
                    )
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
            return Component.newline().append(Colors.PREFIX)
                .append(Component.text("--------------- ", Colors.SPACER))
                .append(Component.text("STATISTIK", Colors.INFO).decorate(TextDecoration.BOLD))
                .append(Component.text(" ---------------", Colors.SPACER))
                .append(Component.newline())
                .append(Colors.PREFIX)
                .append(Component.newline())
                .append(Colors.PREFIX)
                .append(Component.text("Seit Aufzeichnung:", Colors.INFO))
                .append(Component.newline())
                .append(Colors.PREFIX)
                .append(Component.newline())
                .append(Colors.PREFIX)
                .append(Component.text("     - Sprünge: ", Colors.PRIMARY))
                .append(Component.text(points, Colors.VARIABLE_VALUE))
                .append(Component.newline())
                .append(Colors.PREFIX)
                .append(Component.text("     - Rekord: ", Colors.PRIMARY))
                .append(Component.text(highScore, Colors.VARIABLE_VALUE))
                .append(Component.newline())
                .append(Colors.PREFIX)
                .append(Component.text("     - Versuche: ", Colors.PRIMARY))
                .append(Component.text(trys, Colors.VARIABLE_VALUE))
                .append(Component.newline())
                .append(Colors.PREFIX)
                .append(Component.newline())
                .append(Colors.PREFIX)
                .append(Component.text("Aktueller Parkour:", Colors.INFO))
                .append(Component.newline())
                .append(Colors.PREFIX)
                .append(Component.newline())
                .append(Colors.PREFIX)
                .append(Component.text("     - Sprünge: ", Colors.PRIMARY))
                .append(Component.text(current, Colors.VARIABLE_VALUE))
                .append(Component.newline())
                .append(Colors.PREFIX)
                .append(Component.newline())
                .append(Colors.PREFIX)
                .append(Component.text("-----------------------------------------", Colors.SPACER))
        }
    }
}
