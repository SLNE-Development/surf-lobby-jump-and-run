package dev.slne.surf.parkour.minestom.command.subcommand

import dev.slne.minestom.lobby.api.command.commandapi.dsl.playerExecutorSuspend
import dev.slne.minestom.lobby.api.command.commandapi.dsl.subcommand
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.parkour.core.client.command.appendAlreadyInParkour
import dev.slne.surf.parkour.core.client.command.appendParkourStarted
import dev.slne.surf.parkour.core.client.model.parkour.Parkour
import dev.slne.surf.parkour.core.client.permission.ParkourPermissions
import dev.slne.surf.parkour.core.client.service.ParkourService
import dev.slne.surf.parkour.minestom.command.argument.parkourArgument

fun parkourPlayCommand() = subcommand("play") {
    withPermission(ParkourPermissions.COMMAND_PARKOUR_PLAY)
    parkourArgument("parkour")
    playerExecutorSuspend { player, args ->
        val parkour = args.get<Parkour>("parkour")

        ParkourService.getParkourByPlayer(player.uuid)?.let {
            player.sendText { appendAlreadyInParkour(it.displayName) }
            return@playerExecutorSuspend
        }

        val success = parkour.start(player.uuid)

        if (success) {
            player.sendText { appendParkourStarted(parkour.displayName) }
        }
    }
}
