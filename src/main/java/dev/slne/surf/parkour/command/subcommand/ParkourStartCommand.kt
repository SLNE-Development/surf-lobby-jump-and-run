package dev.slne.surf.parkour.command.subcommand

import com.github.shynixn.mccoroutine.bukkit.launch
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

class ParkourStartCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(Permission.COMMAND_PARKOUR_START)
        withOptionalArguments(ParkourArgument("parkour"))

        executesPlayer(PlayerCommandExecutor { player: Player, args: CommandArguments ->
            val parkour = args.getUnchecked<Parkour?>("parkour") ?: DatabaseProvider.getParkours().first()

            if(parkour == null) {
                SurfParkour.send(player, MessageBuilder().primary("Es wurde ").error("kein Parkour gefunden").primary("."))
                return@PlayerCommandExecutor
            }

            if(Parkour.isJumping(player)){
                SurfParkour.send(player, MessageBuilder().primary("Du ").error("befindest dich bereits ").primary("in einem Parkour."))
                return@PlayerCommandExecutor
            }

            dev.slne.surf.parkour.instance.launch {
                parkour.startParkour(player)
                parkour.announceNewParkourStarted(player, parkour.name)
            }
        })
    }
}
