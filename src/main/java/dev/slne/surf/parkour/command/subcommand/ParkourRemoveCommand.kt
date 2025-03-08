package dev.slne.surf.parkour.command.subcommand

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.parkour.SurfParkour
import dev.slne.surf.parkour.command.argument.ParkourArgument
import dev.slne.surf.parkour.database.DatabaseProvider
import dev.slne.surf.parkour.parkour.Parkour
import dev.slne.surf.parkour.util.MessageBuilder
import dev.slne.surf.parkour.util.Permission
import org.bukkit.entity.Player

class ParkourRemoveCommand(commandName: String): CommandAPICommand(commandName) {
    init {
        withPermission(Permission.COMMAND_PARKOUR_REMOVE)
        withArguments(ParkourArgument("parkour"))

        executesPlayer(PlayerCommandExecutor { player: Player, args: CommandArguments ->
            val parkour = args.getUnchecked<Parkour>("parkour") ?: return@PlayerCommandExecutor

            DatabaseProvider.getParkours().remove(parkour)

            SurfParkour.send(player, MessageBuilder().primary("Du hast den Parkour ").variableValue(parkour.name).success(" erfolgreich gelöscht").primary("."))
        })
    }
}