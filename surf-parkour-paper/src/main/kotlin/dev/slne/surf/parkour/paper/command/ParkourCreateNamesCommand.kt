package dev.slne.surf.parkour.paper.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.api.paper.command.executors.anyExecutorSuspend
import dev.slne.surf.parkour.core.client.command.recreateMissingPlayerNames
import dev.slne.surf.parkour.paper.permission.ParkourPermissionRegistry

fun CommandAPICommand.parkourCreateNamesCommand() = subcommand("reCreateNames") {
    withPermission(ParkourPermissionRegistry.COMMAND_PARKOUR_NAMES)
    anyExecutorSuspend { sender, _ ->
        recreateMissingPlayerNames(sender)
    }
}
