package dev.slne.surf.parkour.gui.categories

import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder
import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import dev.slne.surf.parkour.SurfParkour
import dev.slne.surf.parkour.database.DatabaseProvider
import dev.slne.surf.parkour.gui.ParkourMenu
import dev.slne.surf.parkour.instance
import dev.slne.surf.parkour.parkour.Parkour
import dev.slne.surf.parkour.util.ItemBuilder
import dev.slne.surf.parkour.util.MessageBuilder
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player


class ParkourStartGameMenu(player: Player) : ChestGui(
    5,
    ComponentHolder.of(MessageBuilder().primary("ᴘᴀʀᴋᴏᴜʀ ᴀᴜsᴡäʜʟᴇɴ").build().decorate(TextDecoration.BOLD))
) {
    init {
        //objectlist
        val parkourDisplayItemList = ObjectArrayList<GuiItem>()
        //panes
        val outlinePane = StaticPane(0, 0, 9, 5)
        val pages = PaginatedPane(1, 1, 7, 3)
        //items
        val outlineItem =
            GuiItem(ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(Component.text(" ")).build())
        val backButton = GuiItem(
            ItemBuilder(Material.ARROW).setName(MessageBuilder().error("Vorherige Seite").build()).addLoreLine(MessageBuilder().info("Klicke, um die Seite zu wechseln!").build()).build()
        ) {
            if (pages.page > 0) {
                pages.page -= 1
                update()
            }
        }
        val continueButton = GuiItem(
            ItemBuilder(Material.ARROW).setName(MessageBuilder().success("Nächste Seite").build()).addLoreLine(MessageBuilder().info("Klicke, um die Seite zu wechseln!").build()).build()
        ) {
            if (pages.page < pages.pages - 1) {
                pages.page += 1
                update()

            }
        }
        val menuButton =
            GuiItem(ItemBuilder(Material.BARRIER).setName(MessageBuilder().primary("Hautmenü").build()).addLoreLine(MessageBuilder().info("Klicke, um zum Hautmenü zurückzukehren!").build()).build())
            {
                ParkourMenu(player)
            }
        //outlinepane with page buttons
        for (y in 0 until 5) {
            for (x in 0 until 9) {
                if (y == 4) {
                    outlinePane.addItem(outlineItem, 1, y)
                    outlinePane.addItem(outlineItem, 3, y)
                    outlinePane.addItem(outlineItem, 5, y)
                    outlinePane.addItem(outlineItem, 7, y)
                }
                if (y == 0) {
                    outlinePane.addItem(outlineItem, x, y)
                } else {
                    if (x == 0 || x == 8) {
                        outlinePane.addItem(outlineItem, x, y)
                    }
                }
            }
        }
        outlinePane.addItem(backButton, 2, 4)
        outlinePane.addItem(menuButton, 4, 4)
        outlinePane.addItem(continueButton, 6, 4)


        for (parkour in DatabaseProvider.getParkours()) {
            val parkourItem = GuiItem(
                ItemBuilder(Material.COMPASS)
                    .setName(MessageBuilder().primary(parkour.name).build())
                    .addLoreLine(MessageBuilder().info("Klicke, um diesen Parkour zu spielen!").build())
                    .build()
            ) { _ ->
                instance.launch {
                    if(Parkour.isJumping(player)){
                        SurfParkour.send(player, MessageBuilder().primary("Du ").error("befindest dich bereits ").primary("in einem Parkour."))
                        return@launch
                    }
                    parkour.startParkour(player)
                    parkour.announceNewParkourStarted(player, parkour.name)
                }

                player.closeInventory()
            }

            parkourDisplayItemList.add(parkourItem)
        }


        pages.populateWithGuiItems(parkourDisplayItemList)

        setOnGlobalClick { it.isCancelled = true }
        setOnGlobalDrag { it.isCancelled = true }

        addPane(outlinePane)
        addPane(pages)

        show(player)
    }
}