package dev.slne.surf.parkour.menu

import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder
import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane
import com.github.stefvanschie.inventoryframework.pane.Pane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import dev.slne.surf.parkour.database.DatabaseProvider
import dev.slne.surf.parkour.instance
import dev.slne.surf.parkour.util.ItemBuilder
import dev.slne.surf.parkour.util.MessageBuilder
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player


class ParkourSelectionMenu(player: Player) : ChestGui(
    5,
    ComponentHolder.of(MessageBuilder().primary("ᴘᴀʀᴋᴏᴜʀ ᴀᴜsᴡäʜʟᴇɴ").build().decorate(TextDecoration.BOLD))
) {
    init {
        //objectlist
        val items = ObjectArrayList<GuiItem>()
        //panes
        val outlinePane = StaticPane(0, 0, 9, 4)
        val pages = PaginatedPane(1, 1, 7, 3)
        val taskbar = StaticPane(0, 4, 9, 1)
        //items
        val outlineItem = GuiItem(ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(Component.text(" ")).build())
        val backButton = GuiItem(
            ItemBuilder(Material.RED_CONCRETE).setName(MessageBuilder().error("Vorherige Seite").build()).build()
        ) {
            if (pages.page > 0) {
                pages.page -= 1
                update()
            }
        }
        val continueButton = GuiItem(
            ItemBuilder(Material.LIME_CONCRETE).setName(MessageBuilder().success("Nächste Seite").build()).build()
        ) {
            if (pages.page < pages.pages - 1) {
                pages.page += 1
                update()
            }
        }
        val menuButton =
            GuiItem(ItemBuilder(Material.BARRIER).setName(MessageBuilder().info("Home").build()).build())

        //add items to the panes
        //add buttons to the taskbar
        taskbar.addItem(menuButton, 4, 0)
        taskbar.addItem(backButton, 2, 0)
        taskbar.addItem(continueButton, 6, 0)
        //full up the rest with outline items
        for (x in 0 until 9) {
            if (x != 2 && x != 4 && x != 6) {
                taskbar.addItem(outlineItem, x, 0)
            }
        }

        //add items for outline
        for (x in 0 until 9) {
            outlinePane.addItem(outlineItem, x, 0)
            outlinePane.addItem(outlineItem, x, 4)

        }

        for (y in 1 until 4) {
            outlinePane.addItem(outlineItem, 0, y)
            outlinePane.addItem(outlineItem, 8, y)
        }



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

        setOnGlobalClick { it.isCancelled = true }
        setOnGlobalDrag { it.isCancelled = true }

        addPane(outlinePane)
        addPane(taskbar)
        addPane(pages)

        show(player)
    }
}