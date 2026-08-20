package dev.slne.surf.parkour.paper.command

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.api.paper.inventory.framework.viewFrame
import dev.slne.surf.parkour.core.client.menu.ParkourLeaderboardPreferences
import dev.slne.surf.parkour.paper.menu.view.ParkourOverviewView
import dev.slne.surf.parkour.paper.permission.ParkourPermissionRegistry

fun parkourCommand() = commandAPICommand("parkour") {
    withPermission(ParkourPermissionRegistry.COMMAND_PARKOUR)
    parkourPlayCommand()
    parkourListCommand()
    parkourReloadCommand()
    parkourCreateCommand()
    parkourStatsCommand()
    parkourCreateNamesCommand()

    playerExecutor { player, _ ->
        ParkourLeaderboardPreferences.setSearch(player.uniqueId, null)
        viewFrame.open(ParkourOverviewView::class.java, player)
    }
}
