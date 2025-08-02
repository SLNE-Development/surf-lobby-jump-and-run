package dev.slne.surf.parkour.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.parkour.util.ParkourPermissionRegistry
import org.bukkit.Location

fun CommandAPICommand.parkourCreateCommand() = subcommand("create") {
    withPermission(ParkourPermissionRegistry.COMMAND_PARKOUR_CREATE)
    stringArgument("name")
    locationArgument("corner1")
    locationArgument("corner2")
    locationArgument("spawn")
    locationArgument("respawn")
    anyExecutor { executor, args ->
        val name: String by args
        val corner1: Location by args
        val corner2: Location by args
        val spawn: Location by args
        val respawn: Location by args


    }
}