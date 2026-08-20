package dev.slne.surf.parkour.minestom.command

import dev.slne.minestom.lobby.api.command.commandapi.dsl.commandAPICommand
import dev.slne.minestom.lobby.api.command.commandapi.dsl.playerExecutor
import dev.slne.surf.api.minestom.inventory.framework.open
import dev.slne.surf.parkour.core.client.menu.ParkourLeaderboardPreferences
import dev.slne.surf.parkour.core.client.permission.ParkourPermissions
import dev.slne.surf.parkour.minestom.command.subcommand.parkourCreateCommand
import dev.slne.surf.parkour.minestom.command.subcommand.parkourCreateNamesCommand
import dev.slne.surf.parkour.minestom.command.subcommand.parkourListCommand
import dev.slne.surf.parkour.minestom.command.subcommand.parkourPlayCommand
import dev.slne.surf.parkour.minestom.command.subcommand.parkourReloadCommand
import dev.slne.surf.parkour.minestom.command.subcommand.parkourStatsCommand
import dev.slne.surf.parkour.minestom.menu.view.ParkourOverviewView

fun parkourCommand() = commandAPICommand("parkour") {
    withPermission(ParkourPermissions.COMMAND_PARKOUR)
    withSubcommand(parkourPlayCommand())
    withSubcommand(parkourListCommand())
    withSubcommand(parkourReloadCommand())
    withSubcommand(parkourCreateCommand())
    withSubcommand(parkourStatsCommand())
    withSubcommand(parkourCreateNamesCommand())

    playerExecutor { player, _ ->
        ParkourLeaderboardPreferences.setSearch(player.uuid, null)
        ParkourOverviewView.open(player)
    }
}
