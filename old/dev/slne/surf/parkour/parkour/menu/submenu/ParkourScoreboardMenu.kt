package dev.slne.surf.parkour.menu.submenu

import com.github.shynixn.mccoroutine.folia.globalRegionDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mccoroutine.folia.ticks
import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import dev.slne.surf.parkour.api.model.parkour.statistic.ParkourStatisticSummary
import dev.slne.surf.parkour.core.service.parkourStatisticsService
import dev.slne.surf.parkour.menu.AbstractParkourGui
import dev.slne.surf.parkour.menu.type.LeaderboardSortingType
import dev.slne.surf.parkour.menu.util.*
import dev.slne.surf.surfapi.bukkit.api.builder.buildItem
import dev.slne.surf.surfapi.bukkit.api.builder.buildLore
import dev.slne.surf.surfapi.bukkit.api.builder.displayName
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import dev.slne.surf.surfapi.core.api.util.toObjectList
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.event.inventory.ClickType

class ParkourScoreboardMenu(
    override val statistics: ParkourStatisticSummary,
    private var sorting: LeaderboardSortingType
) :
    AbstractParkourGui(5, buildText {
        primary("Bestenliste".toSmallCaps())
        decorate(TextDecoration.BOLD)
    }, statistics) {

    private val outlineItem = outlineItem()
    private val outlinePane = StaticPane(0, 0, 9, 5).apply {
        fillBorder(outlineItem)
        addItem(menuButton(), 4, 4)
        addItem(GuiItem(buildItem(Material.COMPASS) {
            displayName { primary("Sortieren nach: ${sorting.displayName}") }
            buildLore {
                line { info("Klicke, um die Sortierung zu ändern!") }
                LeaderboardSortingType.entries.forEach { type ->
                    line {
                        darkSpacer(if (type == sorting) "> " else "  ")
                        info(type.displayName)
                    }
                }
            }
        }) {
            sorting = when (it.click) {
                ClickType.LEFT -> {
                    sorting.next()
                }

                else -> {
                    sorting.previous()
                }
            }

            ParkourScoreboardMenu(statistics, sorting).show(it.whoClicked)
        }, 4, 0)
    }

    private val pages = PaginatedPane(1, 1, 7, 3)
    private val backButton = backButton(pages)
    private val continueButton = nextButton(pages)

    init {
        lazilyAddStatisticsItems()
        addPane(outlinePane)
        addPane(pages)
    }

    override fun update() {
        updatePaginationButtons(outlinePane, pages, outlineItem, backButton, continueButton)
        super.update()
    }

    private fun lazilyAddStatisticsItems() {
        plugin.launch {
            val statistics = parkourStatisticsService.getEverySummary()

            sorting.sort(statistics.toObjectList())

            val guiItemsDeferred = statistics
                .map { async { it.asStatisticsItem() } }
                .toMutableList()

            while (guiItemsDeferred.isNotEmpty()) {
                val completedItems = guiItemsDeferred.filter { it.isCompleted }
                guiItemsDeferred -= completedItems.toSet()

                val guiItems = completedItems.awaitAll()
                if (guiItems.isNotEmpty()) {
                    withContext(plugin.globalRegionDispatcher) {
                        pages.populateWithGuiItems(guiItems)
                        update()
                    }
                }

                delay(10.ticks)
            }
        }
    }

    private suspend fun ParkourStatisticSummary.asStatisticsItem() =
        GuiItem(HeadUtil.getPlayerHead(uuid).apply {
            displayName(text(name))
            buildLore {
                emptyLine()
                line { info("Statistiken:") }
                line {
                    spacer("  - ")
                    variableKey("Sprünge: ".toSmallCaps())
                    variableValue(statistics.totalJumps.toString())
                }
                line {
                    spacer("  - ")
                    variableKey("Versuche: ".toSmallCaps())
                    variableValue(statistics.totalTries.toString())
                }
                line {
                    spacer("  - ")
                    variableKey("Highscore: ".toSmallCaps())
                    variableValue(statistics.bestJumps.toString())
                }
            }
        })
}