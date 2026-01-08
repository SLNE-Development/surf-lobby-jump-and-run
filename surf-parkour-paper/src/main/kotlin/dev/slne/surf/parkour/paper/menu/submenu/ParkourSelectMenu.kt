package dev.slne.surf.parkour.paper.menu.submenu

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import dev.slne.surf.parkour.paper.menu.AbstractParkourGui
import dev.slne.surf.parkour.paper.menu.ParkourMenu
import dev.slne.surf.parkour.paper.menu.type.RedirectType
import dev.slne.surf.parkour.paper.menu.util.*
import dev.slne.surf.parkour.paper.model.parkour.Parkour
import dev.slne.surf.parkour.paper.model.parkour.PersonalParkourSummary
import dev.slne.surf.parkour.paper.service.parkourService
import dev.slne.surf.surfapi.bukkit.api.builder.buildItem
import dev.slne.surf.surfapi.bukkit.api.builder.displayName
import dev.slne.surf.surfapi.bukkit.api.builder.lore
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent

class ParkourSelectMenu(
    override val statistics: PersonalParkourSummary,
    private val redirect: RedirectType
) :
    AbstractParkourGui(5, buildText {
        primary("Parkour wählen".toSmallCaps())
        decorate(TextDecoration.BOLD)
    }, statistics) {

    private val outlineItem = outlineItem()
    private val outlinePane = StaticPane(0, 0, 9, 5).apply {
        fillBorder(outlineItem)
        addItem(menuButton(), 4, 4)
    }

    private val pages = PaginatedPane(1, 1, 7, 3)
    private val backButton = backButton(pages)
    private val continueButton = nextButton(pages)

    private val parkourItems = parkourService.getParkours().map { parkour ->
        GuiItem(buildItem(Material.COMPASS) {
            displayName { text(parkour.displayName) }
            lore { info("Klicke, um den Parkour auszuwählen.") }
        }) { it.handleParkourSelect(parkour) }
    }

    init {
        pages.populateWithGuiItems(parkourItems)

        addPane(outlinePane)
        addPane(pages)
    }

    override fun update() {
        updatePaginationButtons(outlinePane, pages, outlineItem, backButton, continueButton)
        super.update()
    }

    private fun InventoryClickEvent.handleParkourSelect(parkour: Parkour) {
        when (redirect) {
            RedirectType.MAIN -> plugin.launch(plugin.entityDispatcher(player)) {
                ParkourMenu(statistics).open(player)
            }

            RedirectType.PARKOUR_ACTIVES -> {
                if (parkour.players.isEmpty()) {
                    ParkourGeneralFailureMenu(
                        statistics,
                        buildText { error("Es sind keine Spieler in diesem Parkour.") }
                    ).show(whoClicked)
                    return
                }
                plugin.launch(plugin.entityDispatcher(player)) {
                    ParkourActivePlayersMenu(parkour, statistics).open(player)
                }
            }

            RedirectType.START_PARKOUR -> plugin.launch { parkour.start(player.uniqueId) }
                .also { player.closeInventory() }
        }
    }
}
