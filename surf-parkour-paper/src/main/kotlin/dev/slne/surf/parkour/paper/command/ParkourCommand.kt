package dev.slne.surf.parkour.paper.command

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.parkour.paper.menu.sort.ParkourLeaderboardSortType
import dev.slne.surf.parkour.paper.menu.view.ParkourOverviewView
import dev.slne.surf.parkour.paper.permission.ParkourPermissionRegistry
import dev.slne.surf.surfapi.bukkit.api.inventory.framework.viewFrame

fun parkourCommand() = commandAPICommand("parkour") {
    withPermission(ParkourPermissionRegistry.COMMAND_PARKOUR)
    parkourPlayCommand()
    parkourListCommand()
    parkourReloadCommand()
    parkourCreateCommand()
    parkourStatsCommand()
    parkourCreateNamesCommand()

    playerExecutor { player, _ ->
        ParkourLeaderboardSortType.setSearch(player.uniqueId, null)
        viewFrame.open(ParkourOverviewView::class.java, player)
    }
}