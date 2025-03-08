package dev.slne.surf.parkour.command.subcommand

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.IntegerArgument
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.parkour.SurfParkour
import dev.slne.surf.parkour.command.argument.ParkourArgument
import dev.slne.surf.parkour.parkour.Parkour
import dev.slne.surf.parkour.util.MessageBuilder
import dev.slne.surf.parkour.util.PageableMessageBuilder
import dev.slne.surf.parkour.util.Permission
import dev.slne.surf.parkour.util.playerName
import org.bukkit.entity.Player

class ParkourListCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(Permission.COMMAND_PARKOUR_LIST)
        withArguments(ParkourArgument("parkour"))
        withOptionalArguments(IntegerArgument("page"))
        executesPlayer(PlayerCommandExecutor { player: Player, args: CommandArguments ->
            val parkour = args.getUnchecked<Parkour>("parkour") ?: return@PlayerCommandExecutor
            val page = args.getOrDefaultUnchecked("page", 1)
            val message = PageableMessageBuilder()

            if(parkour.activePlayers.isEmpty()) {
                SurfParkour.send(player, MessageBuilder().error("Es sind keine Spieler in ").info(parkour.name).error(" aktiv."))
                return@PlayerCommandExecutor
            }

            message.setPageCommand("/parkour list ${parkour.name} %page%")
            message.setTitle(MessageBuilder().primary("Spieler in ").info(parkour.name).build())

            for (activePlayer in parkour.activePlayers) {
                message.addLine(MessageBuilder().darkSpacer("- ").variableValue(activePlayer.playerName()).darkSpacer(" (${parkour.currentPoints.getInt(activePlayer)})").build())
            }

            message.send(player, page)
        })
    }
}
