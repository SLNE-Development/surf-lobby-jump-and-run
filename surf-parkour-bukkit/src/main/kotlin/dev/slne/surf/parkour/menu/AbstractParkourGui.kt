package dev.slne.surf.parkour.menu

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder
import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import dev.slne.surf.parkour.api.model.parkour.statistic.ParkourStatisticSummary
import dev.slne.surf.parkour.menu.util.cancelGlobalClick
import dev.slne.surf.parkour.menu.util.cancelGlobalDrag
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import net.kyori.adventure.text.Component
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

abstract class AbstractParkourGui(
    rows: Int,
    title: Component,
    override val statistics: ParkourStatisticSummary
) : ChestGui(rows, ComponentHolder.of(title)), PlayerDataHolderGui {
    private val updatingItems = mutableObject2ObjectMapOf<GuiItem, () -> ItemStack>()

    init {
        cancelGlobalClick()
        cancelGlobalDrag()
    }

    fun updatingItem(stack: () -> ItemStack, action: (InventoryClickEvent) -> Unit) =
        GuiItem(stack(), action).also {
            updatingItems[it] = stack
        }

    override fun update() {
        updatingItems.forEach { (guiItem, stack) ->
            guiItem.item = stack()
        }

        super.update()
    }
}