package dev.slne.surf.parkour.paper.newmenu.view

import dev.slne.surf.parkour.api.data.ParkourStats
import dev.slne.surf.parkour.paper.newmenu.dialog.searchParkourStatsDialog
import dev.slne.surf.parkour.paper.newmenu.sort.ParkourLeaderboardSortType
import dev.slne.surf.parkour.paper.newmenu.util.MenuHeads
import dev.slne.surf.parkour.paper.newmenu.util.outlineItem
import dev.slne.surf.parkour.paper.service.parkourService
import dev.slne.surf.surfapi.bukkit.api.builder.buildItem
import dev.slne.surf.surfapi.bukkit.api.builder.buildLore
import dev.slne.surf.surfapi.bukkit.api.builder.displayName
import dev.slne.surf.surfapi.bukkit.api.inventory.framework.titleBuilder
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.playSound
import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
import me.devnatan.inventoryframework.View
import me.devnatan.inventoryframework.ViewConfigBuilder
import me.devnatan.inventoryframework.context.Context
import me.devnatan.inventoryframework.context.RenderContext
import me.devnatan.inventoryframework.context.SlotClickContext
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.inventory.meta.SkullMeta
import java.util.*

@Suppress("UnstableApiUsage")
object ParkourLeaderboardView : View() {
    private val selectedSort = mutableState(ParkourLeaderboardSortType.HIGHSCORE)

    private val previousItem = MenuHeads.ARROW_LEFT.clone().apply {
        displayName {
            auctionColored("Vorherige Seite")
        }
    }

    private val nextItem = MenuHeads.ARROW_RIGHT.clone().apply {
        displayName {
            auctionColored("Nächste Seite")
        }
    }

    private val searchItem = buildItem(Material.BRUSH) {
        displayName {
            auctionColored("Suchen")
        }
    }

    private fun sortItem(state: ParkourLeaderboardSortType) = buildItem(Material.COMPARATOR) {
        displayName {
            auctionColored("Sortieren")
        }

        buildLore {
            emptyLine()
            line { auctionColored("Sortierung".toSmallCaps(), TextDecoration.BOLD) }

            line {
                if (state == ParkourLeaderboardSortType.HIGHSCORE) {
                    appendSpace()
                    spacer("-")
                    appendSpace()
                    auctionColored("Highscore")
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
                    auctionColored("Meiste Gesamtsprünge")
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
                    auctionColored("Wenigste Gesamtsprünge")
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
                    auctionColored("Meiste Versuche")
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
                    auctionColored("Wenigste Versuche")
                } else {
                    spacer("-")
                    appendSpace()
                    white("Wenigste Versuche")
                }
            }
        }
    }

    private val paginationState = buildComputedPaginationState<ParkourStats> { context ->
        getParkourStatsSortedAndFiltered(
            ParkourLeaderboardSortType.sortingByPlayer(context.player.uniqueId),
            ParkourLeaderboardSortType.searchByPlayer(context.player.uniqueId)
        ).toMutableList()
    }.elementFactory { context, builder, _, auction ->
        builder.withItem(createStatsItem(auction, context.player.uniqueId)).onClick { context ->
            context.playGeneralClickSound()
        }
    }.layoutTarget('R').build()

    override fun onInit(config: ViewConfigBuilder) {
        config
            .titleBuilder {
                auctionColored("Parkourstatistiken".toSmallCaps(), TextDecoration.BOLD)
            }
            .size(6)
            .layout(
                "OOOOOOOOO",
                "ORRRRRRRO",
                "ORRRRRRRO",
                "ORRRRRRRO",
                "ORRRRRRRO",
                "UAOPONOOS"
            )
            .cancelInteractions()
    }

    override fun onFirstRender(render: RenderContext) {
        selectedSort.set(ParkourLeaderboardSortType.sortingByPlayer(render.player.uniqueId), render)

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
            .renderWith {
                previousItem
            }
            .watch(paginationState)
            .displayIf { _ -> paginationState.get(render).canBack() }
            .onClick { context ->
                context.playNewPageSound()
                paginationState.get(render).back()
            }

        render
            .layoutSlot('N')
            .renderWith {
                nextItem
            }
            .watch(paginationState)
            .displayIf { _ -> paginationState.get(render).canAdvance() }
            .onClick { context ->
                context.playNewPageSound()
                paginationState.get(render).advance()
            }
    }

    override fun onUpdate(update: Context) {
        updatePagination(update)
    }

    override fun onResume(origin: Context, target: Context) {
        target.update()
    }

    private fun updatePagination(context: Context) {
        val pagination = paginationState.get(context)
        pagination.switchTo(pagination.currentPageIndex())
    }
}

fun createStatsItem(stats: ParkourStats, viewer: UUID) = buildItem(Material.PLAYER_HEAD) {
    editMeta(SkullMeta::class.java) {
        // TODO: Player Skin
    }

    buildLore {
        emptyLine()
        line {
            auctionColored("Parkourstatistiken".toSmallCaps(), TextDecoration.BOLD)
        }
        line {
            spacer("-")
            appendSpace()
            auctionColored("Highscore: ")
            variableValue(stats.highscore)
        }
        line {
            spacer("-")
            appendSpace()
            auctionColored("Versuche: ")
            variableValue(stats.totalRuns)
        }
        line {
            spacer("-")
            appendSpace()
            auctionColored("Gesamtsprünge: ")
            variableValue(stats.totalJumps)
        }
        line {
            spacer("-")
            appendSpace()
            auctionColored("Durschnittliche Zeit: ")
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


fun SurfComponentBuilder.auctionColored(text: Any, vararg decoration: TextDecoration) =
    coloredComponent(text.toString(), TextColor.color(59, 245, 239), *decoration)