package dev.slne.surf.parkour.minestom.command.subcommand

import dev.slne.minestom.lobby.api.command.commandapi.dsl.anyExecutor
import dev.slne.minestom.lobby.api.command.commandapi.dsl.subcommand
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.parkour.core.client.command.appendReloadFinished
import dev.slne.surf.parkour.core.client.command.appendReloadStarted
import dev.slne.surf.parkour.core.client.config.ParkourConfiguration
import dev.slne.surf.parkour.core.client.permission.ParkourPermissions

fun parkourReloadCommand() = subcommand("reload") {
    withPermission(ParkourPermissions.COMMAND_PARKOUR_RELOAD)
    anyExecutor { executor, _ ->
        executor.sendText { appendReloadStarted() }

        ParkourConfiguration.reload()

        executor.sendText { appendReloadFinished() }
    }
}
