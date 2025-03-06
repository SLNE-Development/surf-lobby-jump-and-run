package dev.slne.surf.parkour.gui.categories

import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder
import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import dev.slne.surf.parkour.database.DatabaseProvider
import dev.slne.surf.parkour.gui.ParkourMenu
import dev.slne.surf.parkour.instance
import dev.slne.surf.parkour.leaderboard.LeaderboardSortingType
import dev.slne.surf.parkour.util.ItemBuilder
import dev.slne.surf.parkour.util.MessageBuilder
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unimi.dsi.fastutil.objects.ObjectList
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player

class ParkourScoreboardMenu(player: Player, sorting: LeaderboardSortingType) : ChestGui(
    5,
    ComponentHolder.of(MessageBuilder().primary("ʙᴇsᴛᴇɴʟɪsᴛᴇ").build().decorate(TextDecoration.BOLD))
) {
    private var sortingType = sorting

    init {
        instance.launch {
            val items: ObjectList<GuiItem> = ObjectArrayList()
            val outlinePane = StaticPane(0, 0, 9, 5)
            val pages = PaginatedPane(1, 1, 7, 3)
            val outlineItem = GuiItem(ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(Component.text(" ")).build())
            val backButton = GuiItem(ItemBuilder(Material.ARROW).setName(MessageBuilder().error("Vorherige Seite").build()).addLoreLine(MessageBuilder().info("Klicke, um die Seite zu wechseln!").build()).build()) {
                if (pages.page > 0) {
                    pages.page -= 1
                    update()
                }
            }

            val continueButton = GuiItem(ItemBuilder(Material.ARROW).setName(MessageBuilder().success("Nächste Seite").build()).addLoreLine(MessageBuilder().info("Klicke, um die Seite zu wechseln!").build()).build()) {
                if (pages.page < pages.pages - 1) {
                    pages.page += 1
                    update()
                }
            }

            val menuButton = GuiItem(ItemBuilder(Material.BARRIER).setName(MessageBuilder().primary("Hautmenü").build()).addLoreLine(MessageBuilder().info("Klicke, um zum Hautmenü zurückzukehren!").build()).build()) {
                ParkourMenu(player)
            }

            val cycleButton = GuiItem(ItemBuilder(Material.COMPASS)
                .setName(MessageBuilder().primary("Sortieren nach: $sortingType").build())
                .addLoreLine(MessageBuilder().info("Klicke, um die Sortierung zu ändern!").build())
                .build()) {
                sortingType = LeaderboardSortingType.entries[(sortingType.ordinal + 1) % LeaderboardSortingType.entries.size]

                ParkourScoreboardMenu(player, sortingType)
            }

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

            for (everyPlayerData in DatabaseProvider.getEveryPlayerData(sortingType)) {
                items.add(GuiItem(ItemBuilder(Material.PLAYER_HEAD)
                    .setSkullOwner(player)
                    .setName(MessageBuilder(everyPlayerData.name).build())
                    .addLoreLine(Component.empty())
                    .addLoreLine(MessageBuilder().info("Statistiken:").build())
                    .addLoreLine(MessageBuilder().darkSpacer("  - ").variableKey("ѕᴘʀüɴɢᴇ: ").variableValue("${everyPlayerData.points}").build())
                    .addLoreLine(MessageBuilder().darkSpacer("  - ").variableKey("ᴠᴇʀѕᴜᴄʜᴇ: ").variableValue("${everyPlayerData.trys}").build())
                    .addLoreLine(MessageBuilder().darkSpacer("  - ").variableKey("ʜɪɢʜѕᴄᴏʀᴇ: ").variableValue("${everyPlayerData.highScore}").build())
                    .build()))
            }

            outlinePane.addItem(backButton, 2, 4)
            outlinePane.addItem(menuButton, 4, 4)
            outlinePane.addItem(continueButton, 6, 4)
            outlinePane.addItem(cycleButton, 4, 0)

            setOnGlobalDrag { it.isCancelled = true }
            setOnGlobalClick { it.isCancelled = true }

            addPane(outlinePane)
            show(player)
        }
    }
}