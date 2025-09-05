package dev.slne.surf.parkour.menu.submenu

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import dev.slne.surf.parkour.api.model.parkour.statistic.ParkourStatisticSummary
import dev.slne.surf.parkour.menu.AbstractParkourGui
import dev.slne.surf.parkour.menu.ParkourMenu
import dev.slne.surf.parkour.menu.util.fillOuterBorder
import dev.slne.surf.parkour.menu.util.outlineItem
import dev.slne.surf.surfapi.bukkit.api.builder.buildItem
import dev.slne.surf.surfapi.bukkit.api.builder.displayName
import dev.slne.surf.surfapi.bukkit.api.builder.lore
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player

class ParkourGeneralFailureMenu(
    override val statistics: ParkourStatisticSummary,
    title: Component
) :
    AbstractParkourGui(5, buildText {
        error("Ups...".toSmallCaps())
        decorate(TextDecoration.BOLD)
    }, statistics) {
    init {
        val outlinePane = StaticPane(0, 0, 9, 5).apply {
            fillOuterBorder(outlineItem())
        }

        val failurePane = StaticPane(4, 2, 1, 1).apply {
            val failureItem = GuiItem(buildItem(Material.BARRIER) {
                displayName(title)
                lore { info("Klicke, um zum Hautmenü zurückzukehren!") }
            }) {
                ParkourMenu.lazyOpen(
                    it.whoClicked as? Player ?: error("Clicker must be a player")
                )
            }

            addItem(failureItem, 0, 0)
        }


        addPane(outlinePane)
        addPane(failurePane)
    }
}