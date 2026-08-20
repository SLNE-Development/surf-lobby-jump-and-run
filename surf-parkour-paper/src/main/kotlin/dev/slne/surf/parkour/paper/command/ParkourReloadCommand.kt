package dev.slne.surf.parkour.paper.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.parkour.core.client.command.appendReloadFinished
import dev.slne.surf.parkour.core.client.command.appendReloadStarted
import dev.slne.surf.parkour.core.client.config.ParkourConfiguration
import dev.slne.surf.parkour.paper.permission.ParkourPermissionRegistry

fun CommandAPICommand.parkourReloadCommand() = subcommand("reload") {
    withPermission(ParkourPermissionRegistry.COMMAND_PARKOUR_RELOAD)
    anyExecutor { executor, _ ->
        executor.sendText { appendReloadStarted() }

        ParkourConfiguration.reload()

        executor.sendText { appendReloadFinished() }
    }
}
