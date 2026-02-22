package dev.slne.surf.parkour.paper.newmenu.dialog

import dev.slne.surf.parkour.paper.newmenu.sort.ParkourLeaderboardSortType
import dev.slne.surf.parkour.paper.newmenu.view.ParkourLeaderboardView
import dev.slne.surf.parkour.paper.newmenu.view.auctionColored
import dev.slne.surf.surfapi.bukkit.api.dialog.search.searchDialog
import dev.slne.surf.surfapi.bukkit.api.inventory.framework.viewFrame

@Suppress("UnstableApiUsage")
fun searchParkourStatsDialog() = searchDialog(
    title = {
        auctionColored("Suche ein Spieler...")
    },
    searchInput = {

    },
    body = {
        plainMessage {
            auctionColored("Gib den Namen eines Spielers ein, um nach Statistiken zu suchen.")
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