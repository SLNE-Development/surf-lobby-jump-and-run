package dev.slne.surf.parkour.paper.menu.view

import dev.slne.surf.parkour.api.data.ParkourStats
import dev.slne.surf.parkour.paper.menu.dialog.searchParkourStatsDialog
import dev.slne.surf.parkour.paper.menu.sort.ParkourLeaderboardSortType
import dev.slne.surf.parkour.paper.menu.util.*
import dev.slne.surf.parkour.paper.service.parkourService
import dev.slne.surf.parkour.paper.service.playerTextureService
import dev.slne.surf.parkour.paper.util.formatMillis
import dev.slne.surf.parkour.paper.util.playerHead
import dev.slne.surf.parkour.paper.util.playerName
import dev.slne.surf.surfapi.bukkit.api.builder.buildItem
import dev.slne.surf.surfapi.bukkit.api.builder.buildLore
import dev.slne.surf.surfapi.bukkit.api.builder.displayName
import dev.slne.surf.surfapi.bukkit.api.inventory.framework.titleBuilder
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.playSound
import me.devnatan.inventoryframework.View
import me.devnatan.inventoryframework.ViewConfigBuilder
import me.devnatan.inventoryframework.component.Pagination
import me.devnatan.inventoryframework.context.RenderContext
import me.devnatan.inventoryframework.context.SlotClickContext
import me.devnatan.inventoryframework.state.State
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.Sound

@Suppress("UnstableApiUsage")
object ParkourLeaderboardView : View() {
    private val selectedSort = mutableState(ParkourLeaderboardSortType.HIGHSCORE)

    private val searchItem = buildItem(Material.BRUSH) {
        displayName {
            parkourColored("Suchen")
        }
    }

    private fun sortItem(state: ParkourLeaderboardSortType) = buildItem(Material.COMPARATOR) {
        displayName {
            parkourColored("Sortieren")
        }

        buildLore {
            emptyLine()
            line { parkourColored("Sortierung".toSmallCaps(), TextDecoration.BOLD) }

            line {
                if (state == ParkourLeaderboardSortType.HIGHSCORE) {
                    appendSpace()
                    spacer("-")
                    appendSpace()
                    parkourColored("Highscore")
                } else {
                    spacer("-")
                    appendSpace()
                    white("Highscore")
                }
            }

            line {
                if (state == ParkourLeaderboardSortType.MOST_JUMPS) {
                    appendSpace()
                    spacer("-")
                    appendSpace()
                    parkourColored("Meiste Gesamtsprünge")
                } else {
                    spacer("-")
                    appendSpace()
                    white("Meiste Gesamtsprünge")
                }
            }

            line {
                if (state == ParkourLeaderboardSortType.LEAST_JUMPS) {
                    appendSpace()
                    spacer("-")
                    appendSpace()
                    parkourColored("Wenigste Gesamtsprünge")
                } else {
                    spacer("-")
                    appendSpace()
                    white("Wenigste Gesamtsprünge")
                }
            }

            line {
                if (state == ParkourLeaderboardSortType.MOST_TRIES) {
                    appendSpace()
                    spacer("-")
                    appendSpace()
                    parkourColored("Meiste Versuche")
                } else {
                    spacer("-")
                    appendSpace()
                    white("Meiste Versuche")
                }
            }

            line {
                if (state == ParkourLeaderboardSortType.LEAST_TRIES) {
                    appendSpace()
                    spacer("-")
                    appendSpace()
                    parkourColored("Wenigste Versuche")
                } else {
                    spacer("-")
                    appendSpace()
                    white("Wenigste Versuche")
                }
            }
        }
    }

    private val paginationState: State<Pagination> =
        buildLazyPaginationState<ParkourStats> { context ->
            getParkourStatsSortedAndFiltered(
                ParkourLeaderboardSortType.sortingByPlayer(context.player.uniqueId),
                ParkourLeaderboardSortType.searchByPlayer(context.player.uniqueId)
            ).toMutableList()
        }.elementFactory { context, builder, index, stats ->
            val currentSort = selectedSort.get(context)

            builder.withItem(createStatsItem(stats, index + 1, currentSort)).onClick { clickContext ->
                clickContext.playGeneralClickSound()
            }
        }.layoutTarget('R').build()

    override fun onInit(config: ViewConfigBuilder) {
        config
            .titleBuilder {
                parkourColored("Parkourstatistiken".toSmallCaps(), TextDecoration.BOLD)
            }
            .size(6)
            .layout(
                "OOOOOOOOO",
                "ORRRRRRRO",
                "ORRRRRRRO",
                "ORRRRRRRO",
                "ORRRRRRRO",
                "OAOPBNOOS"
            )
            .cancelInteractions()
    }

    override fun onFirstRender(render: RenderContext) {
        val pagination = paginationState.get(render)
        selectedSort.set(ParkourLeaderboardSortType.sortingByPlayer(render.player.uniqueId), render)

        render.layoutSlot('B', backItem).onClick { context ->
            context.openForPlayer(ParkourOverviewView::class.java)
        }

        render
            .layoutSlot('S')
            .updateOnClick()
            .renderWith { sortItem(selectedSort.get(render)) }
            .onClick { context ->
                context.playGeneralClickSound()

                if (context.isRightClick) {
                    selectedSort.set(selectedSort.get(render).previous(), render)
                } else {
                    selectedSort.set(selectedSort.get(render).next(), render)
                }

                ParkourLeaderboardSortType.setSorting(
                    context.player.uniqueId,
                    selectedSort.get(render)
                )

                context.openForPlayer(ParkourLeaderboardView::class.java) // INFO: This is needed, as IF has current bugs when using computed pagination states. The author is aware of this.
            }
        render.layoutSlot('O', outlineItem)
        render.layoutSlot('A', searchItem).onClick { context ->
            context.playGeneralClickSound()
            context.player.closeInventory()
            context.player.showDialog(searchParkourStatsDialog())
        }

        render
            .layoutSlot('P')
            .updateOnStateChange(paginationState)
            .displayIf { _ ->
                pagination.canBack()
            }
            .onRender { slotRender ->
                if (pagination.canBack()) {
                    slotRender.item = previousItem
                } else {
                    slotRender.item = outlineItem
                }
            }
            .onClick { context ->
                pagination.back()
                pagination.update()
                context.playNewPageSound()
            }

        render
            .layoutSlot('N')
            .updateOnStateChange(paginationState)
            .displayIf { _ ->
                pagination.canAdvance()
            }
            .onRender { slotRender ->
                if (pagination.canAdvance()) {
                    slotRender.item = nextItem
                } else {
                    slotRender.item = outlineItem
                }
            }
            .onClick { context ->
                pagination.advance()
                pagination.update()
                context.playNewPageSound()
            }
    }
}

fun createStatsItem(stats: ParkourStats, rank: Int, sortType: ParkourLeaderboardSortType) = stats.playerUuid.playerHead().apply {
    displayName {
        parkourColored(playerTextureService.getTexture(stats.playerUuid).playerName)
    }

    buildLore {
        emptyLine()
        line {
            parkourColored("Platzierung".toSmallCaps(), TextDecoration.BOLD)
            appendSpace()
            when (sortType) {
                ParkourLeaderboardSortType.HIGHSCORE -> {
                    spacer("(Highscore)", TextDecoration.BOLD)
                }

                ParkourLeaderboardSortType.MOST_JUMPS -> {
                    spacer("(Meiste Gesamtsprünge)", TextDecoration.BOLD)
                }

                ParkourLeaderboardSortType.LEAST_JUMPS -> {
                    spacer("(Wenigste Gesamtsprünge)", TextDecoration.BOLD)
                }

                ParkourLeaderboardSortType.MOST_TRIES -> {
                    spacer("(Meiste Versuche)", TextDecoration.BOLD)
                }

                ParkourLeaderboardSortType.LEAST_TRIES -> {
                    spacer("(Wenigste Versuche)", TextDecoration.BOLD)
                }
            }
        }
        line {
            spacer("-")
            appendSpace()
            parkourColored("Rang: ")
            variableValue(rank)
        }

        line {
            parkourColored("Parkourstatistiken".toSmallCaps(), TextDecoration.BOLD)
        }
        line {
            spacer("-")
            appendSpace()
            parkourColored("Highscore: ")
            variableValue(stats.highscore)
        }
        line {
            spacer("-")
            appendSpace()
            parkourColored("Versuche: ")
            variableValue(stats.totalRuns)
        }
        line {
            spacer("-")
            appendSpace()
            parkourColored("Gesamtsprünge: ")
            variableValue(stats.totalJumps)
        }
        line {
            spacer("-")
            appendSpace()
            parkourColored("Durchschnittliche Zeit: ")
            variableValue(formatMillis(stats.averageTime))
        }
    }
}

fun SlotClickContext.playGeneralClickSound() {
    player.playSound(true) {
        type(Sound.UI_BUTTON_CLICK)
    }
}

fun SlotClickContext.playNewPageSound() {
    player.playSound(true) {
        type(Sound.ENTITY_CHICKEN_EGG)
    }
}

private fun getParkourStatsSortedAndFiltered(
    sortType: ParkourLeaderboardSortType,
    search: String?
): List<ParkourStats> {
    val base = parkourService.stats

    val filtered = if (search.isNullOrBlank()) {
        base
    } else {
        base.filter { stats ->
            stats.playerName.lowercase().contains(search.lowercase())
        }
    }

    return when (sortType) {
        ParkourLeaderboardSortType.HIGHSCORE -> filtered.sortedByDescending { it.highscore }
        ParkourLeaderboardSortType.MOST_JUMPS -> filtered.sortedByDescending { it.totalJumps }
        ParkourLeaderboardSortType.LEAST_JUMPS -> filtered.sortedBy { it.totalJumps }
        ParkourLeaderboardSortType.MOST_TRIES -> filtered.sortedByDescending { it.totalRuns }
        ParkourLeaderboardSortType.LEAST_TRIES -> filtered.sortedBy { it.totalRuns }
    }
}