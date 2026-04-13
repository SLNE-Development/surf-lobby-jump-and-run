package dev.slne.surf.parkour.paper.menu.dialog

import dev.slne.surf.api.paper.dialog.search.searchDialog
import dev.slne.surf.api.paper.inventory.framework.viewFrame
import dev.slne.surf.parkour.paper.menu.sort.ParkourLeaderboardSortType
import dev.slne.surf.parkour.paper.menu.util.parkourColored
import dev.slne.surf.parkour.paper.menu.view.ParkourLeaderboardView

@Suppress("UnstableApiUsage")
fun searchParkourStatsDialog() = searchDialog(
    title = {
        parkourColored("Suche ein Spieler...")
    },
    searchInput = {

    },
    body = {
        plainMessage {
            parkourColored("Gib den Namen eines Spielers ein, um nach Statistiken zu suchen.")
        }
    },
    onSearch = { player, query ->
        ParkourLeaderboardSortType.setSearch(player.uniqueId, query)
        viewFrame.open(ParkourLeaderboardView::class.java, player)
    },
    onClose = { player, query ->
        ParkourLeaderboardSortType.setSearch(player.uniqueId, query)
        viewFrame.open(ParkourLeaderboardView::class.java, player)
    }
)