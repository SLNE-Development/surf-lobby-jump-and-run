package dev.slne.surf.parkour.gui.categories

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder
import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.StaticPane

import dev.slne.surf.parkour.gui.ParkourMenu
import dev.slne.surf.parkour.util.ItemBuilder
import dev.slne.surf.parkour.util.MessageBuilder

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration

import org.bukkit.Material
import org.bukkit.entity.Player

class ParkourGeneralFailureMenu(player: Player, title: MessageBuilder) : ChestGui(5, ComponentHolder.of(MessageBuilder().error("ᴜᴘѕ...").build().decorate(TextDecoration.BOLD))) {
    init {
        val outlinePane = StaticPane(0, 0, 9, 5)
        val outlineItem = GuiItem(ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(Component.text(" ")).build())

        val failurePane = StaticPane(4, 2, 1, 1)
        val failureItem = GuiItem(ItemBuilder(Material.BARRIER).setName(title.build()).addLoreLine(MessageBuilder().info("Klicke, um zum Hautmenü zurückzukehren!").build()).build()) {
            ParkourMenu(player)
        }

        for (x in 0 until 9) {
            outlinePane.addItem(outlineItem, x, 0)
            outlinePane.addItem(outlineItem, x, 4)
        }

        for (y in 1 until 4) {
            outlinePane.addItem(outlineItem, 0, y)
            outlinePane.addItem(outlineItem, 8, y)
        }
        
        failurePane.addItem(failureItem, 0, 0)


        addPane(outlinePane)
        addPane(failurePane)

        setOnGlobalClick { it.isCancelled = true }
        setOnGlobalDrag { it.isCancelled = true }

        show(player)
    }
}