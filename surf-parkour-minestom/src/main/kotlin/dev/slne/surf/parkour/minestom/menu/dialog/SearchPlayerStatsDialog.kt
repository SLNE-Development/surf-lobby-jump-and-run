package dev.slne.surf.parkour.minestom.menu.dialog

import dev.slne.surf.api.minestom.dialog.search.searchDialog
import dev.slne.surf.api.minestom.inventory.framework.open
import dev.slne.surf.parkour.core.client.menu.ParkourLeaderboardPreferences
import dev.slne.surf.parkour.core.client.menu.ParkourSearchTexts.appendSearchBody
import dev.slne.surf.parkour.core.client.menu.ParkourSearchTexts.appendSearchTitle
import dev.slne.surf.parkour.minestom.menu.view.ParkourLeaderboardView

fun searchParkourStatsDialog() = searchDialog(
    title = { appendSearchTitle() },
    searchInput = {

    },
    body = {
        plainMessage { appendSearchBody() }
    },
    onSearch = { player, query ->
        ParkourLeaderboardPreferences.setSearch(player.uuid, query)
        ParkourLeaderboardView.open(player)
    },
    onClose = { player, query ->
        ParkourLeaderboardPreferences.setSearch(player.uuid, query)
        ParkourLeaderboardView.open(player)
    }
)
