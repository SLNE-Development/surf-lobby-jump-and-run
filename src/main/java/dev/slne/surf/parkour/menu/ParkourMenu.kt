package dev.slne.surf.parkour.menu

import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder
import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import dev.slne.surf.parkour.database.DatabaseProvider
import dev.slne.surf.parkour.instance
import dev.slne.surf.parkour.util.Colors
import dev.slne.surf.parkour.util.ItemBuilder
import dev.slne.surf.parkour.util.MessageBuilder
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player


class ParkourMenu(player: Player): ChestGui(5, ComponentHolder.of(MessageBuilder().primary("Parkour").build())) {
    init {
        instance.launch {
            val playerData = DatabaseProvider.getPlayerData(player.uniqueId)
            val jumps = playerData.points
            val tries = playerData.trys
            val highscore = playerData.highScore
            val name = playerData.name

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

            val playerHeadPane = StaticPane(4, 1, 1, 1)

            val profileHead = ItemBuilder(Material.PLAYER_HEAD)
                .applySkullMeta(player)
                .setName(Component.text(name, Colors.GOLD))
                .addLoreLine(Component.text("Sprünge: ", Colors.DARK_AQUA).append(Component.text("$jumps", Colors.PRIMARY)))
                .addLoreLine(Component.text("Versuche: ", Colors.DARK_AQUA).append(Component.text("$tries", Colors.PRIMARY)))
                .addLoreLine(Component.text("HighScore ", Colors.DARK_AQUA).append(Component.text("$highscore", Colors.PRIMARY)))
                .build()

            playerHeadPane.addItem(GuiItem(profileHead), 0, 0)

            val taskbarPane = StaticPane(0, 3, 9, 1)
            val statsItem = GuiItem(ItemBuilder(Material.NETHER_STAR).setName(Component.text("Bestenliste", Colors.DARK_AQUA)).addLoreLine(Component.text("Klicke, um dir die Bestenliste anzusehen!",Colors.GRAY)).build())
            val startItem = GuiItem(ItemBuilder(Material.RECOVERY_COMPASS).setName(Component.text("Parkour starten", Colors.DARK_AQUA)).addLoreLine(Component.text("Klicke, um einen Parkour zu starten!",Colors.GRAY)).build()) { event -> event.isCancelled = true }
            val settingsItem = GuiItem(ItemBuilder(Material.REPEATING_COMMAND_BLOCK).setName(Component.text("Einstellungen", Colors.DARK_AQUA)).addLoreLine(Component.text("Klicke, um zu den Einstellungen zu gelangen!",Colors.GRAY)).build())
            val activePlayersItem = GuiItem(ItemBuilder(Material.WRITABLE_BOOK).setName(Component.text("Aktive Spieler", Colors.DARK_AQUA)).addLoreLine(Component.text("Klicke, um dir die aktiven Spieler anzusehen!",Colors.GRAY)).build())

            taskbarPane.addItem(statsItem, 1, 0)
            taskbarPane.addItem(startItem, 3, 0)
            taskbarPane.addItem(settingsItem, 5, 0)
            taskbarPane.addItem(activePlayersItem, 7, 0)

            setOnGlobalDrag { it.isCancelled = true }
            setOnGlobalClick { it.isCancelled = true }

            addPane(taskbarPane)
            addPane(outlinePane)
            addPane(playerHeadPane)

            show(player);
        }
    }
}
