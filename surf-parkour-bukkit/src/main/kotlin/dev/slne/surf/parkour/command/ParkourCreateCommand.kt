package dev.slne.surf.parkour.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.parkour.util.ParkourPermissionRegistry

fun CommandAPICommand.parkourCreateCommand() = subcommand("create") {
    withPermission(ParkourPermissionRegistry.COMMAND_PARKOUR_CREATE)
}