package dev.slne.surf.parkour.minestom.command.subcommand

import dev.slne.minestom.lobby.api.command.commandapi.dsl.anyExecutor
import dev.slne.minestom.lobby.api.command.commandapi.dsl.integerArgument
import dev.slne.minestom.lobby.api.command.commandapi.dsl.subcommand
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.parkour.core.client.command.appendNoParkourFound
import dev.slne.surf.parkour.core.client.command.renderParkourList
import dev.slne.surf.parkour.core.client.permission.ParkourPermissions
import dev.slne.surf.parkour.core.client.service.ParkourService

fun parkourListCommand() = subcommand("list") {
    withPermission(ParkourPermissions.COMMAND_PARKOUR_LIST)
    integerArgument("page", 1, Int.MAX_VALUE, optional = true)
    anyExecutor { executor, args ->
        val page = args.getOptional<Int>("page") ?: 1
        val parkours = ParkourService.parkours

        if (parkours.isEmpty()) {
            executor.sendText { appendNoParkourFound() }
            return@anyExecutor
        }

        executor.sendText {
            append(renderParkourList(parkours, page))
        }
    }
}
