package dev.slne.surf.parkour.paper.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.parkour.core.client.command.appendAlreadyInParkour
import dev.slne.surf.parkour.core.client.command.appendParkourStarted
import dev.slne.surf.parkour.core.client.model.parkour.Parkour
import dev.slne.surf.parkour.core.client.platform.ParkourPlatform
import dev.slne.surf.parkour.core.client.service.ParkourService
import dev.slne.surf.parkour.paper.command.argument.parkourArgument
import dev.slne.surf.parkour.paper.permission.ParkourPermissionRegistry

fun CommandAPICommand.parkourPlayCommand() = subcommand("play") {
    withPermission(ParkourPermissionRegistry.COMMAND_PARKOUR_PLAY)
    parkourArgument("parkour")
    playerExecutor { player, args ->
        val parkour: Parkour by args

        ParkourPlatform.launch {
            ParkourService.getParkourByPlayer(player.uniqueId)?.let {
                player.sendText { appendAlreadyInParkour(it.displayName) }
                return@launch
            }

            val success = parkour.start(player.uniqueId)

            if (success) {
                player.sendText { appendParkourStarted(parkour.displayName) }
            }
        }
    }
}
