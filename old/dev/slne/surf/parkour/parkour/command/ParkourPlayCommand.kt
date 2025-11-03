package dev.slne.surf.parkour.command

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.parkour.api.model.parkour.Parkour
import dev.slne.surf.parkour.command.argument.parkourArgument
import dev.slne.surf.parkour.core.service.parkourService
import dev.slne.surf.parkour.plugin
import dev.slne.surf.parkour.util.ParkourPermissionRegistry
import dev.slne.surf.parkour.util.parkourPlayer

fun CommandAPICommand.parkourPlayCommand() = subcommand("play") {
    withPermission(ParkourPermissionRegistry.COMMAND_PARKOUR_PLAY)
    parkourArgument("parkour")
    playerExecutor { player, args ->
        val parkour: Parkour by args

        plugin.launch {
            val parkourPlayer = player.parkourPlayer()

            parkourService.getParkour(parkourPlayer)?.let {
                parkourPlayer.sendText {
                    appendPrefix()
                    error("Du bist bereits in dem Parkour ")
                    variableValue(it.name)
                    error(".")
                }
                return@launch
            }

            parkour.start(parkourPlayer)
            parkourPlayer.sendText {
                appendPrefix()
                success("Du hast den Parkour ")
                variableValue(parkour.name)
                success(" gestartet.")
            }
        }
    }
}