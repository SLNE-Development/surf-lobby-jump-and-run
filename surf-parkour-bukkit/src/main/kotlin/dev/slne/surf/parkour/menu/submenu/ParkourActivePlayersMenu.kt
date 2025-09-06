package dev.slne.surf.parkour.menu.submenu

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder
import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import dev.slne.surf.parkour.api.model.parkour.Parkour
import dev.slne.surf.parkour.api.model.parkour.statistic.ParkourStatisticSummary
import dev.slne.surf.parkour.menu.PlayerDataHolderGui
import dev.slne.surf.parkour.menu.util.*
import dev.slne.surf.surfapi.bukkit.api.builder.buildItem
import dev.slne.surf.surfapi.bukkit.api.builder.displayName
import dev.slne.surf.surfapi.bukkit.api.builder.lore
import dev.slne.surf.surfapi.bukkit.api.builder.meta
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.SkullMeta

class ParkourActivePlayersMenu(
    val parkour: Parkour,
    override val statistics: ParkourStatisticSummary
) : PlayerDataHolderGui {
    suspend fun open(player: Player) {
        val gui = ChestGui(5, ComponentHolder.of(buildText {
            primary("Aktive Spieler".toSmallCaps())
            decorate(TextDecoration.BOLD)
        }))
        val outlineItem = gui.outlineItem()
        val outlinePane = StaticPane(0, 0, 9, 5).apply {
            fillBorder(outlineItem)
            addItem(menuButton(), 4, 4)
        }

        val pages = PaginatedPane(1, 1, 7, 3)
        val backButton = gui.backButton(pages)
        val continueButton = gui.nextButton(pages)

        val activePlayers = parkour.players
        val playerList = activePlayers.mapTo(mutableObjectListOf(activePlayers.size)) {
            GuiItem(buildItem(Material.PLAYER_HEAD) {
                displayName(text(it.name()))
                lore {
                    spacer("  - ")
                    variableKey("Aktuelle Sprünge: ".toSmallCaps())
                    variableValue(parkour.currentIndex(it).toString())
                }
                meta<SkullMeta> { owningPlayer = Bukkit.getPlayer(it) }
            })
        }

        pages.populateWithGuiItems(playerList)

        updatePaginationButtons(outlinePane, pages, outlineItem, backButton, continueButton)

        gui.addPane(outlinePane)
        gui.addPane(pages)

        gui.show(player)
    }
}