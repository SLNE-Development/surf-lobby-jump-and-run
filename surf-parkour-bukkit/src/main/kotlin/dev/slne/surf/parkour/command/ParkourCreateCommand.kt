package dev.slne.surf.parkour.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.parkour.core.model.CoreParkourCreationData
import dev.slne.surf.parkour.core.service.parkourService
import dev.slne.surf.parkour.util.ParkourPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
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

        parkourService.createParkour(
            CoreParkourCreationData(
                name,
                corner1,
                corner2,
                spawn
            )
        )

        executor.sendText {
            appendPrefix()
            success("Du hast den Parkour ")
            variableValue(name)
            success(" erstellt.")
        }
    }
}