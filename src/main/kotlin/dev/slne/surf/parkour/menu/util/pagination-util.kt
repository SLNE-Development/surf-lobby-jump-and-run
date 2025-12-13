package dev.slne.surf.parkour.menu.util

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane
import com.github.stefvanschie.inventoryframework.pane.StaticPane

fun updatePaginationButtons(
    outlinePane: StaticPane,
    pages: PaginatedPane,
    outlineItem: GuiItem,
    backButton: GuiItem,
    continueButton: GuiItem
) {
    if (pages.page > 0) {
        outlinePane.addItem(backButton, 2, 4)
    } else {
        outlinePane.addItem(outlineItem, 2, 4)
    }
    if (pages.page < pages.pages - 1) {
        outlinePane.addItem(continueButton, 6, 4)
    } else {
        outlinePane.addItem(outlineItem, 6, 4)
    }
}