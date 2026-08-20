package dev.slne.surf.parkour.paper.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.integerArgument
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.parkour.core.client.command.appendNoParkourFound
import dev.slne.surf.parkour.core.client.command.renderParkourList
import dev.slne.surf.parkour.core.client.service.ParkourService
import dev.slne.surf.parkour.paper.permission.ParkourPermissionRegistry

fun CommandAPICommand.parkourListCommand() = subcommand("list") {
    withPermission(ParkourPermissionRegistry.COMMAND_PARKOUR_LIST)
    integerArgument("page", 1, Int.MAX_VALUE, optional = true)
    anyExecutor { executor, args ->
        val page = args.getOrDefaultUnchecked("page", 1)
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
