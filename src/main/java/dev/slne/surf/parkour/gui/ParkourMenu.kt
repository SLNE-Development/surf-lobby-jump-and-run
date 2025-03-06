package dev.slne.surf.parkour.gui

import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder
import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import dev.slne.surf.parkour.database.DatabaseProvider
import dev.slne.surf.parkour.gui.categories.*
import dev.slne.surf.parkour.instance
import dev.slne.surf.parkour.leaderboard.LeaderboardSortingType
import dev.slne.surf.parkour.util.ItemBuilder
import dev.slne.surf.parkour.util.MessageBuilder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player


class ParkourMenu(player: Player) :
    ChestGui(5, ComponentHolder.of(MessageBuilder().primary("ᴘᴀʀᴋᴏᴜʀ").build().decorate(TextDecoration.BOLD))) {
    init {
        instance.launch {
            val playerData = DatabaseProvider.getPlayerData(player.uniqueId)
            val jumps = playerData.points
            val tries = playerData.trys
            val highscore = playerData.highScore
            val name = playerData.name

            val outlinePane = StaticPane(0, 0, 9, 5)
            val outlineItem = GuiItem(
                ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(Component.text(" ")).build()
            ) { event -> event.isCancelled = true }
            val closeMenuItem = GuiItem(
                ItemBuilder(Material.BARRIER).setName(MessageBuilder().primary("Schließen").build())
                    .addLoreLine(MessageBuilder().info("Klicke, um das Hautmenü zu schließen!").build()).build()
            ) { player.closeInventory() }

            for (x in 0 until 9) {
                outlinePane.addItem(outlineItem, x, 0)
                if (x == 4) {
                    outlinePane.addItem(closeMenuItem, x, 4)
                } else {
                    outlinePane.addItem(outlineItem, x, 4)
                }
            }
            for (y in 1 until 4) {
                outlinePane.addItem(outlineItem, 0, y)
                outlinePane.addItem(outlineItem, 8, y)
            }

            val playerHeadPane = StaticPane(4, 1, 1, 1)

            val profileHead = ItemBuilder(Material.PLAYER_HEAD)
                .setSkullOwner(player)
                .setName(MessageBuilder(name).build())
                .addLoreLine(Component.empty())
                .addLoreLine(MessageBuilder().info("Statistiken:").build())
                .addLoreLine(MessageBuilder().darkSpacer("  - ").variableKey("ѕᴘʀüɴɢᴇ: ").variableValue("$jumps").build())
                .addLoreLine(MessageBuilder().darkSpacer("  - ").variableKey("ᴠᴇʀѕᴜᴄʜᴇ: ").variableValue("$tries").build())
                .addLoreLine(MessageBuilder().darkSpacer("  - ").variableKey("ʜɪɢʜѕᴄᴏʀᴇ: ").variableValue("$highscore").build())
                .build()

            playerHeadPane.addItem(GuiItem(profileHead), 0, 0)

            val taskbarPane = StaticPane(0, 3, 9, 1)
            val statsItem = GuiItem(
                ItemBuilder(Material.NETHER_STAR)
                    .setName(MessageBuilder("Bestenliste").build())
                    .addLoreLine(MessageBuilder().info("Klicke, um dir die Bestenliste anzusehen!").build())
                    .build()
            ) {
                ParkourScoreboardMenu(player, LeaderboardSortingType.POINTS_HIGHEST)
            }

            val startItem = GuiItem(
                ItemBuilder(Material.RECOVERY_COMPASS)
                    .setName(MessageBuilder("Parkour starten").build())
                    .addLoreLine(MessageBuilder().info("Klicke, um einen Parkour zu starten!").build())
                    .build()
            ) {

                ParkourStartGameMenu(player)
            }

            val settingsItem = GuiItem(
                ItemBuilder(Material.REPEATING_COMMAND_BLOCK)
                    .setName(MessageBuilder("Einstellungen").build())
                    .addLoreLine(MessageBuilder().info("Klicke, um zu den Einstellungen zu gelangen!").build())
                    .build()
            ) {
                ParkourSettingsMenu(player)
            }

            val activePlayersItem = GuiItem(
                ItemBuilder(Material.WRITABLE_BOOK)
                    .setName(MessageBuilder("Aktive Spieler").build())
                    .addLoreLine(MessageBuilder().info("Klicke, um dir die aktiven Spieler anzusehen!").build())
                    .build()
            ) {
                if(DatabaseProvider.getParkours().isEmpty()) {
                    ParkourGeneralFailureMenu(player, MessageBuilder().error("Es gibt keine verfügbaren Parkours!"))
                    return@GuiItem
                }

                if(DatabaseProvider.getParkours().size == 1) {
                    ParkourActivePlayersMenu(player, DatabaseProvider.getParkours().first())
                    return@GuiItem
                }

                ParkourSelectMenu(player, RedirectType.PARKOUR_ACTIVES)
            }

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
