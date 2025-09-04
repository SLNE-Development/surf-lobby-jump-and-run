package dev.slne.surf.parkour.command

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.parkour.core.model.CoreParkourCreationData
import dev.slne.surf.parkour.core.service.parkourService
import dev.slne.surf.parkour.plugin
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

        executor.sendText {
            appendPrefix()
            info("Der Parkour wird erstellt...")
        }

        plugin.launch {
            val parkour = parkourService.createParkour(
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

            parkourService.pushParkour(parkour, plugin.parkourConfig.config.serverUuid)

            executor.sendText {
                appendPrefix()
                info("Der Parkour ist nun für den Server")
                appendSpace()
                variableValue(plugin.parkourConfig.config.serverUuid.toString())
                appendSpace()
                info("gespeichert.")
            }
        }
    }
}