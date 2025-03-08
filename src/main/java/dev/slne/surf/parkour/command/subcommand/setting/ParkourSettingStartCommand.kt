package dev.slne.surf.parkour.command.subcommand.setting

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.LocationArgument
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.parkour.SurfParkour
import dev.slne.surf.parkour.command.argument.ParkourArgument
import dev.slne.surf.parkour.parkour.Parkour
import dev.slne.surf.parkour.util.MessageBuilder
import dev.slne.surf.parkour.util.Permission
import org.bukkit.Location
import org.bukkit.entity.Player

class ParkourSettingStartCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(Permission.COMMAND_PARKOUR_SETTING_START)

        withArguments(LocationArgument("pos"))
        withArguments(ParkourArgument("parkour"))

        executesPlayer(PlayerCommandExecutor { player: Player, args: CommandArguments ->
            val pos = args.getUnchecked<Location>("pos") ?: return@PlayerCommandExecutor
            val parkour = args.getUnchecked<Parkour>("parkour") ?: return@PlayerCommandExecutor

            parkour.edit {
                this.start = pos.toVector().normalize()
            }

            SurfParkour.send(player, MessageBuilder().primary("Du hast den Start von ").info(parkour.name).primary(" neu definiert."))
        })
    }
}
