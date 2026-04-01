package dev.slne.surf.parkour.paper.menu.view

import dev.slne.surf.parkour.api.data.ParkourStats
import dev.slne.surf.parkour.core.paper.service.ParkourRunsService
import dev.slne.surf.parkour.core.paper.service.ParkourTexturesService
import dev.slne.surf.parkour.paper.menu.dialog.searchParkourStatsDialog
import dev.slne.surf.parkour.paper.menu.sort.ParkourLeaderboardSortType
import dev.slne.surf.parkour.paper.menu.util.*
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

data class RankedParkourStats(
    val stats: ParkourStats,
    val rank: Int
)

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

            val types = listOf(
                ParkourLeaderboardSortType.HIGHSCORE to "Highscore",
                ParkourLeaderboardSortType.MOST_JUMPS to "Meiste Gesamtsprünge",
                ParkourLeaderboardSortType.LEAST_JUMPS to "Wenigste Gesamtsprünge",
                ParkourLeaderboardSortType.MOST_TRIES to "Meiste Versuche",
                ParkourLeaderboardSortType.LEAST_TRIES to "Wenigste Versuche"
            )

            types.forEach { (type, label) ->
                line {
                    if (state == type) {
                        appendSpace()
                        spacer("-")
                        appendSpace()
                        parkourColored(label)
                    } else {
                        spacer("-")
                        appendSpace()
                        white(label)
                    }
                }
            }
        }
    }

    private val paginationState: State<Pagination> =
        buildLazyPaginationState<RankedParkourStats> { context ->
            getParkourStatsSortedAndFiltered(
                ParkourLeaderboardSortType.sortingByPlayer(context.player.uniqueId),
                ParkourLeaderboardSortType.searchByPlayer(context.player.uniqueId)
            ).toMutableList()
        }.elementFactory { context, builder, _, rankedData ->
            val currentSort = selectedSort.get(context)
            builder.withItem(createStatsItem(rankedData.stats, rankedData.rank, currentSort))
                .onClick { clickContext ->
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

                val nextSort =
                    if (context.isRightClick) {
                        selectedSort.get(render).previous()
                    } else {
                        selectedSort.get(render).next()
                    }
                selectedSort.set(nextSort, render)

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

fun createStatsItem(stats: ParkourStats, rank: Int, sortType: ParkourLeaderboardSortType) =
    stats.playerUuid.playerHead().apply {
        val texture = ParkourTexturesService.getTexture(stats.playerUuid)

        displayName {
            parkourColored(texture.playerName)
        }

        buildLore {
            emptyLine()
            line {
                parkourColored("Platzierung".toSmallCaps(), TextDecoration.BOLD)
                appendSpace()
                when (sortType) {
                    ParkourLeaderboardSortType.HIGHSCORE -> {
                        spacer("(Highscore)")
                    }

                    ParkourLeaderboardSortType.MOST_JUMPS -> {
                        spacer(
                            "(Meiste Gesamtsprünge)"
                        )
                    }

                    ParkourLeaderboardSortType.LEAST_JUMPS -> {
                        spacer("(Wenigste Gesamtsprünge)")
                    }

                    ParkourLeaderboardSortType.MOST_TRIES -> {
                        spacer("(Meiste Versuche)")
                    }

                    ParkourLeaderboardSortType.LEAST_TRIES -> {
                        spacer("(Wenigste Versuche)")
                    }
                }
            }
            line {
                spacer("-")
                appendSpace()
                parkourColored("Rang: ")
                variableValue("#$rank")
            }
            emptyLine()
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
    player.playSound(true) { type(Sound.UI_BUTTON_CLICK) }
}

fun SlotClickContext.playNewPageSound() {
    player.playSound(true) { type(Sound.ENTITY_CHICKEN_EGG) }
}

private fun getParkourStatsSortedAndFiltered(
    sortType: ParkourLeaderboardSortType,
    search: String?
): List<RankedParkourStats> {
    val base = ParkourRunsService.stats

    val sortedGlobal = when (sortType) {
        ParkourLeaderboardSortType.HIGHSCORE -> base.sortedByDescending { it.highscore }
        ParkourLeaderboardSortType.MOST_JUMPS -> base.sortedByDescending { it.totalJumps }
        ParkourLeaderboardSortType.LEAST_JUMPS -> base.sortedBy { it.totalJumps }
        ParkourLeaderboardSortType.MOST_TRIES -> base.sortedByDescending { it.totalRuns }
        ParkourLeaderboardSortType.LEAST_TRIES -> base.sortedBy { it.totalRuns }
    }

    val rankedList = sortedGlobal.mapIndexed { index, stats ->
        RankedParkourStats(stats, index + 1)
    }

    return if (search.isNullOrBlank()) {
        rankedList
    } else {
        val query = search.lowercase()
        rankedList.filter {
            it.stats.playerName.lowercase().contains(query)
        }
    }
}