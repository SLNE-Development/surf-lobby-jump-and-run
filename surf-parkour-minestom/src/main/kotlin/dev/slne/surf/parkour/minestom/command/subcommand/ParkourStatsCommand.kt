package dev.slne.surf.parkour.minestom.command.subcommand

import dev.slne.minestom.lobby.api.command.commandapi.dsl.playerExecutor
import dev.slne.minestom.lobby.api.command.commandapi.dsl.subcommand
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.parkour.core.client.command.appendPersonalStats
import dev.slne.surf.parkour.core.client.permission.ParkourPermissions
import dev.slne.surf.parkour.core.client.service.ParkourRunsService

fun parkourStatsCommand() = subcommand("stats") {
    withPermission(ParkourPermissions.COMMAND_PARKOUR_STATS)
    playerExecutor { player, _ ->
        player.sendText {
            appendPersonalStats(ParkourRunsService.getStats(player.uuid))
        }
    }
}
