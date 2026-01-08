package dev.slne.surf.parkour.paper.menu.util

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane
import dev.slne.surf.parkour.paper.menu.ParkourMenu
import dev.slne.surf.parkour.paper.menu.PlayerDataHolderGui
import dev.slne.surf.parkour.paper.plugin
import dev.slne.surf.surfapi.bukkit.api.builder.buildItem
import dev.slne.surf.surfapi.bukkit.api.builder.displayName
import dev.slne.surf.surfapi.bukkit.api.builder.lore
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import org.bukkit.Material

fun Gui.backButton(pages: PaginatedPane) = GuiItem(buildItem(Material.ARROW) {
    displayName { primary("Vorherige Seite") }
    lore { info("Klicke, um die Seite zu wechseln!") }
}) { previousPage(pages) }

fun Gui.nextButton(pages: PaginatedPane) = GuiItem(buildItem(Material.ARROW) {
    displayName { primary("Nächste Seite") }
    lore { info("Klicke, um die Seite zu wechseln!") }
}) { nextPage(pages) }

fun Gui.outlineItem() = GuiItem(buildItem(Material.GRAY_STAINED_GLASS_PANE) {
    displayName(text(" "))
})

fun PlayerDataHolderGui.menuButton() = GuiItem(buildItem(Material.BARRIER) {
    displayName { primary("Hauptmenü") }
    lore { info("Klicke, um zum Hauptmenü zurückzukehren!") }
}) {
    plugin.launch(plugin.entityDispatcher(it.player)) {
        ParkourMenu(statistics).open(it.player)
    }
}