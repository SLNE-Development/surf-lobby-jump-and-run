package dev.slne.surf.parkour.paper.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.parkour.core.client.command.appendPersonalStats
import dev.slne.surf.parkour.core.client.service.ParkourRunsService
import dev.slne.surf.parkour.paper.permission.ParkourPermissionRegistry

fun CommandAPICommand.parkourStatsCommand() = subcommand("stats") {
    withPermission(ParkourPermissionRegistry.COMMAND_PARKOUR_STATS)
    playerExecutor { player, _ ->
        player.sendText {
            appendPersonalStats(ParkourRunsService.getStats(player.uniqueId))
        }
    }
}
