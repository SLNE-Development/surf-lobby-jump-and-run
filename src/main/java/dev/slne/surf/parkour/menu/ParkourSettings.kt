package dev.slne.surf.parkour.menu

import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder
import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import dev.slne.surf.parkour.database.DatabaseProvider
import dev.slne.surf.parkour.instance
import dev.slne.surf.parkour.util.ItemBuilder
import dev.slne.surf.parkour.util.MessageBuilder
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unimi.dsi.fastutil.objects.ObjectList
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player

class ParkourSettings(player: Player) : ChestGui(
    5,
    ComponentHolder.of(MessageBuilder().primary("ᴇɪɴsᴛᴇʟʟᴜɴɢᴇɴ").build().decorate(TextDecoration.BOLD))
) {
    init {
        val outlinePane = StaticPane(0, 0, 9, 5)
        val outlineItem = GuiItem(
            ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(Component.text(" ")).build()
        ) { event -> event.isCancelled = true }

        for (x in 0 until 9) {
            outlinePane.addItem(outlineItem, x, 0)
            outlinePane.addItem(outlineItem, x, 4)
        }

        for (y in 1 until 4) {
            outlinePane.addItem(outlineItem, 0, y)
            outlinePane.addItem(outlineItem, 8, y)
        }

        val pages = PaginatedPane(1, 1, 7, 3)
        val items: ObjectList<GuiItem> = ObjectArrayList()

        for (parkour in DatabaseProvider.getParkours()) {
            val parkourItem = GuiItem(
                ItemBuilder(Material.PAPER)
                    .setName(MessageBuilder().primary(parkour.name).build())
                    .addLoreLine(MessageBuilder().info("Klicke, um diesen Parkour zu spielen!").build())
                    .build()
            ) { _ ->
                instance.launch {
                    parkour.startParkour(player)
                    parkour.announceNewParkourStarted(player, parkour.name)
                }

                player.closeInventory()
            }

            items.add(parkourItem)
        }

        pages.populateWithGuiItems(items)

        addPane(outlinePane)
        addPane(pages)
        setOnGlobalClick { it.isCancelled = true }
        setOnGlobalDrag { it.isCancelled = true }

        show(player)
    }
}