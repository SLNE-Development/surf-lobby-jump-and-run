package dev.slne.surf.parkour.paper.command

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.parkour.paper.command.argument.parkourArgument
import dev.slne.surf.parkour.paper.model.parkour.Parkour
import dev.slne.surf.parkour.paper.permission.ParkourPermissionRegistry
import dev.slne.surf.parkour.paper.plugin
import dev.slne.surf.parkour.paper.service.parkourService
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun CommandAPICommand.parkourPlayCommand() = subcommand("play") {
    withPermission(ParkourPermissionRegistry.COMMAND_PARKOUR_PLAY)
    parkourArgument("parkour")
    playerExecutor { player, args ->
        val parkour: Parkour by args

        plugin.launch {
            parkourService.getParkour(player)?.let {
                player.sendText {
                    appendPrefix()
                    error("Du bist bereits in dem Parkour ")
                    variableValue(it.displayName)
                    error(".")
                }
                return@launch
            }

            parkour.start(player.uniqueId)
            player.sendText {
                appendPrefix()
                success("Du hast den Parkour ")
                variableValue(parkour.displayName)
                success(" gestartet.")
            }
        }
    }
}