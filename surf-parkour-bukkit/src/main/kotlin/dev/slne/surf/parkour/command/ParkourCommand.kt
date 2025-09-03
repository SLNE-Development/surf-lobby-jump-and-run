package dev.slne.surf.parkour.command

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.slne.surf.parkour.util.ParkourPermissionRegistry

fun parkourCommand() = commandAPICommand("parkour") {
    withPermission(ParkourPermissionRegistry.COMMAND_PARKOUR)
    parkourPlayCommand()
    parkourListCommand()
    parkourReloadCommand()
    parkourCreateCommand()
}