package dev.slne.surf.parkour.paper.menu.dialog

import dev.slne.surf.api.paper.dialog.search.searchDialog
import dev.slne.surf.api.paper.inventory.framework.viewFrame
import dev.slne.surf.parkour.core.client.menu.ParkourLeaderboardPreferences
import dev.slne.surf.parkour.core.client.menu.ParkourSearchTexts.appendSearchBody
import dev.slne.surf.parkour.core.client.menu.ParkourSearchTexts.appendSearchTitle
import dev.slne.surf.parkour.paper.menu.view.ParkourLeaderboardView

@Suppress("UnstableApiUsage")
fun searchParkourStatsDialog() = searchDialog(
    title = { appendSearchTitle() },
    searchInput = {

    },
    body = {
        plainMessage { appendSearchBody() }
    },
    onSearch = { player, query ->
        ParkourLeaderboardPreferences.setSearch(player.uniqueId, query)
        viewFrame.open(ParkourLeaderboardView::class.java, player)
    },
    onClose = { player, query ->
        ParkourLeaderboardPreferences.setSearch(player.uniqueId, query)
        viewFrame.open(ParkourLeaderboardView::class.java, player)
    }
)
