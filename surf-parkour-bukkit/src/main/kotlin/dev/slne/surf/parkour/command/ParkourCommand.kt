package dev.slne.surf.parkour.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.slne.surf.parkour.util.ParkourPermissionRegistry

fun CommandAPICommand.parkourCommand() = commandAPICommand("parkour") {
    withPermission(ParkourPermissionRegistry.COMMAND_PARKOUR)
    parkourReloadCommand()
}