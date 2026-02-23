package dev.slne.surf.parkour.paper.menu.view

import dev.slne.surf.parkour.api.data.ParkourStats
import dev.slne.surf.parkour.paper.menu.dialog.searchParkourStatsDialog
import dev.slne.surf.parkour.paper.menu.sort.ParkourLeaderboardSortType
import dev.slne.surf.parkour.paper.menu.util.*
import dev.slne.surf.parkour.paper.service.parkourService
import dev.slne.surf.surfapi.bukkit.api.builder.buildItem
import dev.slne.surf.surfapi.bukkit.api.builder.buildLore
import dev.slne.surf.surfapi.bukkit.api.builder.displayName
import dev.slne.surf.surfapi.bukkit.api.inventory.framework.titleBuilder
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.playSound
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import me.devnatan.inventoryframework.View
import me.devnatan.inventoryframework.ViewConfigBuilder
import me.devnatan.inventoryframework.component.Pagination
import me.devnatan.inventoryframework.context.Context
import me.devnatan.inventoryframework.context.RenderContext
import me.devnatan.inventoryframework.context.SlotClickContext
import me.devnatan.inventoryframework.state.State
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.inventory.meta.SkullMeta
import java.util.*

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
        buildComputedPaginationState<ParkourStats> { context ->
            getParkourStatsSortedAndFiltered(
                ParkourLeaderboardSortType.sortingByPlayer(context.player.uniqueId),
                ParkourLeaderboardSortType.searchByPlayer(context.player.uniqueId)
            ).toMutableList()
        }.elementFactory { context, builder, _, auction ->
            builder.withItem(createStatsItem(auction, context.player.uniqueId)).onClick { context ->
                context.playGeneralClickSound()
            }
        }.layoutTarget('R')
            .onPageSwitch { context, pagination ->
                context.playNewPageSound()
                context.update()

                context.player.sendText {
                    info(pagination.currentPageIndex())
                }
            }
            .build()

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
                pagination.currentPageIndex() != 0
            }
            .onRender { slotRender ->
                slotRender.item = previousItem
            }
            .onClick { _ ->
                pagination.back()
                pagination.update()
            }

        render
            .layoutSlot('N')
            .updateOnStateChange(paginationState)
            .displayIf { _ ->
                pagination.currentPageIndex() < pagination.lastPageIndex()
            }
            .onRender { slotRender ->
                slotRender.item = nextItem
            }
            .onClick { _ ->
                pagination.advance()
                pagination.update()
            }
    }

    override fun onResume(origin: Context, target: Context) {
        target.update()
    }
}

fun createStatsItem(stats: ParkourStats, viewer: UUID) = buildItem(Material.PLAYER_HEAD) {
    editMeta(SkullMeta::class.java) {
        // TODO: Player Skin
    }

    displayName {
        parkourColored(stats.playerName)
    }

    buildLore {
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
            parkourColored("Durschnittliche Zeit: ")
            variableValue(stats.averageTime) // TODO: Format
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
        ParkourLeaderboardSortType.HIGHSCORE -> filtered.sortedBy { it.highscore }
        ParkourLeaderboardSortType.MOST_JUMPS -> filtered.sortedByDescending { it.totalJumps }
        ParkourLeaderboardSortType.LEAST_JUMPS -> filtered.sortedBy { it.totalJumps }
        ParkourLeaderboardSortType.MOST_TRIES -> filtered.sortedByDescending { it.totalRuns }
        ParkourLeaderboardSortType.LEAST_TRIES -> filtered.sortedBy { it.totalRuns }
    }
}