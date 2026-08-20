package dev.slne.surf.parkour.paper.menu.view

import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.paper.builder.buildItem
import dev.slne.surf.api.paper.builder.displayName
import dev.slne.surf.api.paper.builder.lore
import dev.slne.surf.api.paper.inventory.framework.titleBuilder
import dev.slne.surf.parkour.api.data.ParkourStats
import dev.slne.surf.parkour.core.client.menu.ParkourLeaderboardPreferences
import dev.slne.surf.parkour.core.client.menu.ParkourLeaderboardSortType
import dev.slne.surf.parkour.core.client.menu.ParkourMenuContent
import dev.slne.surf.parkour.core.client.menu.ParkourMenuTitles
import dev.slne.surf.parkour.core.client.menu.parkourStatsFor
import dev.slne.surf.parkour.core.client.menu.playerName
import dev.slne.surf.parkour.core.client.message.parkourColored
import dev.slne.surf.parkour.paper.menu.dialog.searchParkourStatsDialog
import dev.slne.surf.parkour.paper.menu.util.backItem
import dev.slne.surf.parkour.paper.menu.util.nextItem
import dev.slne.surf.parkour.paper.menu.util.outlineItem
import dev.slne.surf.parkour.paper.menu.util.playGeneralClickSound
import dev.slne.surf.parkour.paper.menu.util.playNewPageSound
import dev.slne.surf.parkour.paper.menu.util.previousItem
import dev.slne.surf.parkour.paper.util.playerHead
import me.devnatan.inventoryframework.View
import me.devnatan.inventoryframework.ViewConfigBuilder
import me.devnatan.inventoryframework.component.Pagination
import me.devnatan.inventoryframework.context.RenderContext
import me.devnatan.inventoryframework.state.State
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material

@Suppress("UnstableApiUsage")
object ParkourLeaderboardView : View() {
    private val selectedSort = mutableState(ParkourLeaderboardSortType.HIGHSCORE)

    private val searchItem = buildItem(Material.BRUSH) {
        displayName(ParkourMenuContent.searchName)
    }

    private fun sortItem(state: ParkourLeaderboardSortType) = buildItem(Material.COMPARATOR) {
        displayName(ParkourMenuContent.sortName)
        lore(*ParkourMenuContent.sortLore(state).toTypedArray())
    }

    private val paginationState: State<Pagination> =
        buildLazyPaginationState { context ->
            parkourStatsFor(context.player.uniqueId).toMutableList()
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
                parkourColored(ParkourMenuTitles.STATISTICS.toSmallCaps(), TextDecoration.BOLD)
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
        selectedSort.set(
            ParkourLeaderboardPreferences.sortingByPlayer(render.player.uniqueId),
            render
        )

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

                ParkourLeaderboardPreferences.setSorting(
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
        displayName(ParkourMenuContent.leaderboardEntryName(stats.playerName))
        lore(*ParkourMenuContent.leaderboardEntryLore(stats, rank, sortType).toTypedArray())
    }
