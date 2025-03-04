package dev.slne.surf.parkour.gui

import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import dev.slne.surf.parkour.database.DatabaseProvider
import dev.slne.surf.parkour.instance
import dev.slne.surf.parkour.util.Colors
import dev.slne.surf.parkour.util.ItemBuilder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.Material
import org.bukkit.entity.Player


class ParkourGUI {

    fun createGUI(player: Player) {

        instance.launch {
            val playerData = DatabaseProvider.getPlayerData(player.uniqueId)
            val jumps = playerData.points
            val tries = playerData.trys
            val highscore = playerData.highScore
            val playername = playerData.name

            val gui = ChestGui(5, "Parkour")

            // glass outline
            val outlinePane = StaticPane(0, 0, 9, 5)
            val outlineItem = GuiItem(ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(Component.text(" ")).build()) { event -> event.isCancelled = true }

            for (x in 0 until 9) {
                outlinePane.addItem(outlineItem, x, 0)
                outlinePane.addItem(outlineItem, x, 4)
            }
            for (y in 1 until 4) {
                outlinePane.addItem(outlineItem, 0, y)
                outlinePane.addItem(outlineItem, 8, y)
            }

            val playerheadPane = StaticPane(4, 1, 1, 1)
            val profileHead = ItemBuilder(Material.PLAYER_HEAD)
                .applySkullMeta(player)
                .setName(Component.text(playername, Colors.GOLD))
                .addLoreLine(Component.text("Sprünge: ", Colors.DARK_AQUA).append(Component.text("$jumps", Colors.PRIMARY)))
                .addLoreLine(Component.text("Versuche: ", Colors.DARK_AQUA).append(Component.text("$tries", Colors.PRIMARY)))
                .addLoreLine(Component.text("HighScore ", Colors.DARK_AQUA).append(Component.text("$highscore", Colors.PRIMARY)))
                .build()
            playerheadPane.addItem(GuiItem(profileHead){ event -> event.isCancelled = true }, 0, 0)

            // taskbar 
            val taskbarPane = StaticPane(0, 3, 9, 1)
            val netherStar = GuiItem(ItemBuilder(Material.NETHER_STAR).setName(Component.text("Bestenliste", Colors.DARK_AQUA)).addLoreLine(Component.text("Klicke, um dir die Bestenliste anzusehen!",Colors.GRAY)).build())
            val rocket = GuiItem(ItemBuilder(Material.RECOVERY_COMPASS).setName(Component.text("Parkour starten", Colors.DARK_AQUA)).addLoreLine(Component.text("Klicke, um einen Parkour zu starten!",Colors.GRAY)).build()) { event -> event.isCancelled = true }
            val commandBlock = GuiItem(ItemBuilder(Material.REPEATING_COMMAND_BLOCK).setName(Component.text("Einstellungen", Colors.DARK_AQUA)).addLoreLine(Component.text("Klicke, um zu den Einstellungen zu gelangen!",Colors.GRAY)).build())
            val book = GuiItem(ItemBuilder(Material.WRITABLE_BOOK).setName(Component.text("Aktive Spieler", Colors.DARK_AQUA)).addLoreLine(Component.text("Klicke, um dir die aktiven Spieler anzusehen!",Colors.GRAY)).build())
            
            taskbarPane.addItem(netherStar, 1, 0)  
            taskbarPane.addItem(rocket, 3, 0)     
            taskbarPane.addItem(commandBlock, 5, 0) 
            taskbarPane.addItem(book, 7, 0)

            
            
            gui.addPane(taskbarPane)
            gui.addPane(outlinePane)
            gui.addPane(playerheadPane)
            gui.show(player);

        }
    }
}
