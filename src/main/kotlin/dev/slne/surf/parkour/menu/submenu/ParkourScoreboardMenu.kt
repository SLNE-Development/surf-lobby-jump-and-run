package dev.slne.surf.parkour.menu.submenu

import com.github.shynixn.mccoroutine.folia.globalRegionDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import dev.slne.surf.parkour.menu.AbstractParkourGui
import dev.slne.surf.parkour.menu.type.LeaderboardSortingType
import dev.slne.surf.parkour.menu.util.*
import dev.slne.surf.parkour.model.parkour.PersonalParkourSummary
import dev.slne.surf.parkour.service.parkourService
import dev.slne.surf.surfapi.bukkit.api.builder.buildItem
import dev.slne.surf.surfapi.bukkit.api.builder.buildLore
import dev.slne.surf.surfapi.bukkit.api.builder.displayName
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.service.PlayerLookupService
import kotlinx.coroutines.runBlocking
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.event.inventory.ClickType

class ParkourScoreboardMenu(
    override val statistics: PersonalParkourSummary,
    private var sorting: LeaderboardSortingType
) : AbstractParkourGui(
    5,
    buildText {
        primary("Bestenliste".toSmallCaps())
        decorate(TextDecoration.BOLD)
    },
    statistics
) {

    private val pageSize = 21 // 7×3

    private val outlineItem = outlineItem()

    private val outlinePane = StaticPane(0, 0, 9, 5).apply {
        fillBorder(outlineItem)

        addItem(menuButton(), 4, 4)

        addItem(
            GuiItem(
                buildItem(Material.COMPASS) {
                    displayName {
                        primary("Sortieren nach: ${sorting.displayName}")
                    }
                    buildLore {
                        line { info("Klicke, um die Sortierung zu ändern!") }
                        LeaderboardSortingType.entries.forEach { type ->
                            line {
                                darkSpacer(if (type == sorting) "> " else "  ")
                                info(type.displayName)
                            }
                        }
                    }
                }
            ) {
                sorting = when (it.click) {
                    ClickType.LEFT -> sorting.next()
                    else -> sorting.previous()
                }

                ParkourScoreboardMenu(statistics, sorting).show(it.whoClicked)
            },
            4,
            0
        )
    }

    private val pages = PaginatedPane(1, 1, 7, 3)
    private val backButton = backButton(pages)
    private val continueButton = nextButton(pages)

    init {
        addPane(outlinePane)
        addPane(pages)
    }

    override fun update() {
        val page = pages.page

        val cached = parkourService.getSummaryPage(sorting, page)

        if (cached != null) {
            pages.clear()
            pages.populateWithGuiItems(
                cached.map { runBlocking { it.asStatisticsItem() } }
            )
        } else {
            pages.clear()
            pages.populateWithGuiItems(listOf(loadingItem()))

            parkourService.loadSummaryPage(sorting, page, pageSize) { data ->
                plugin.launch(plugin.globalRegionDispatcher) {
                    if (pages.page != page) {
                        return@launch
                    }

                    pages.clear()
                    pages.populateWithGuiItems(
                        data.map { it.asStatisticsItem() }
                    )
                    update()
                }
            }
        }

        updatePaginationButtons(
            outlinePane,
            pages,
            outlineItem,
            backButton,
            continueButton
        )

        super.update()
    }

    private suspend fun PersonalParkourSummary.asStatisticsItem(): GuiItem =
        GuiItem(
            HeadUtil.getPlayerHead(uuid).apply {
                displayName {
                    text(PlayerLookupService.getUsername(uuid) ?: "???")
                }
                buildLore {
                    emptyLine()
                    line { info("Statistiken:") }

                    line {
                        spacer("  - ")
                        variableKey("Sprünge: ".toSmallCaps())
                        variableValue(totalJumps.toString())
                    }

                    line {
                        spacer("  - ")
                        variableKey("Versuche: ".toSmallCaps())
                        variableValue(totalTries.toString())
                    }

                    line {
                        spacer("  - ")
                        variableKey("Highscore: ".toSmallCaps())
                        variableValue(bestJumps.toString())
                    }
                }
            }
        )

    private fun loadingItem() =
        GuiItem(
            buildItem(Material.CLOCK) {
                displayName { info("Lade Statistiken...") }
            }
        )
}
