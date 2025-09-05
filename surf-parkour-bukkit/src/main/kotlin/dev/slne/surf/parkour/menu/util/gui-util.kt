package dev.slne.surf.parkour.menu.util

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import dev.slne.surf.parkour.util.parkourPlayer
import dev.slne.surf.surfapi.core.api.service.PlayerLookupService
import dev.slne.surf.surfapi.core.api.util.emptyInt2ObjectMap
import dev.slne.surf.surfapi.core.api.util.emptyIntSet
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.IntSet
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import java.util.*

val InventoryClickEvent.player
    get() = whoClicked as? Player ?: error("No player has clicked on the inventory.")

val InventoryClickEvent.parkourPlayer get() = this.player.parkourPlayer()

suspend fun UUID.name() = PlayerLookupService.getUsername(this) ?: "Error"

fun Gui.previousPage(pages: PaginatedPane) {
    if (pages.page > 0) {
        pages.page -= 1
        update()
    }
}

fun Gui.nextPage(pages: PaginatedPane) {
    if (pages.page < pages.pages - 1) {
        pages.page += 1
        update()
    }
}

fun Gui.cancelGlobalClick() = setOnGlobalClick { it.isCancelled = true }
fun Gui.cancelGlobalDrag() = setOnGlobalDrag { it.isCancelled = true }

fun StaticPane.fillTopAndBottomRows(
    borderItem: GuiItem,
    bottomCustom: Int2ObjectMap<GuiItem> = emptyInt2ObjectMap(),
    width: Int = 9,
    height: Int = 5
) {
    for (x in 0 until width) {
        addItem(borderItem, x, 0)
        addItem(bottomCustom[x] ?: borderItem, x, height - 1)
    }
}

fun StaticPane.fillLeftRightColumns(
    borderItem: GuiItem,
    width: Int = 9,
    height: Int = 5
) {
    for (y in 1 until height - 1) {
        addItem(borderItem, 0, y)
        addItem(borderItem, width - 1, y)
    }
}

fun StaticPane.fillOuterBorder(
    borderItem: GuiItem,
    width: Int = 9,
    height: Int = 5
) {
    fillTopAndBottomRows(borderItem, width = width, height = height)
    fillLeftRightColumns(borderItem, width = width, height = height)
}

fun StaticPane.fillTopRow(borderItem: GuiItem, width: Int = 9) {
    for (x in 0 until width) addItem(borderItem, x, 0)
}

fun StaticPane.fillBottomRow(
    borderItem: GuiItem,
    skipPositions: IntSet = emptyIntSet(),
    width: Int = 9,
    row: Int = 4
) {
    for (x in 0 until width) {
        if (x !in skipPositions) addItem(borderItem, x, row)
    }
}

fun StaticPane.fillActivePlayersBorder(borderItem: GuiItem, width: Int = 9, height: Int = 5) {
    for (y in 0 until height) {
        for (x in 0 until width) {
            when {
                y == 0 -> addItem(borderItem, x, y)
                y in 1 until height - 1 && (x == 0 || x == width - 1) -> addItem(borderItem, x, y)
                y == height - 1 && x in listOf(1, 3, 5, 7) -> addItem(borderItem, x, y)
            }
        }
    }
}

fun StaticPane.fillBorder(borderItem: GuiItem, width: Int = 9, height: Int = 5) {
    for (y in 0 until height) {
        for (x in 0 until width) {
            when {
                y == 0 || y == height - 1 -> addItem(borderItem, x, y)
                x == 0 || x == width - 1 -> addItem(borderItem, x, y)
            }
        }
    }
}