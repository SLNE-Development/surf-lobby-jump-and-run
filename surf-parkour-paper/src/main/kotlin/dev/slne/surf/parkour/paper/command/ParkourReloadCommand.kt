package dev.slne.surf.parkour.paper.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.parkour.paper.config.ParkourConfiguration
import dev.slne.surf.parkour.paper.permission.ParkourPermissionRegistry

fun CommandAPICommand.parkourReloadCommand() = subcommand("reload") {
    withPermission(ParkourPermissionRegistry.COMMAND_PARKOUR_RELOAD)
    anyExecutor { executor, _ ->
        executor.sendText {
            appendInfoPrefix()
            info("Das Plugin wird neu geladen...")
        }

        ParkourConfiguration.reload()

        executor.sendText {
            appendSuccessPrefix()
            success("Das Plugin wurde neu geladen.")
        }
    }
}