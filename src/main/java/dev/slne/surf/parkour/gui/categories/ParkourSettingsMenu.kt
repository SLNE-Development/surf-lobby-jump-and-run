package dev.slne.surf.parkour.gui.categories

import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder
import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import dev.slne.surf.parkour.SurfParkour
import dev.slne.surf.parkour.database.DatabaseProvider
import dev.slne.surf.parkour.gui.ParkourMenu
import dev.slne.surf.parkour.instance
import dev.slne.surf.parkour.util.ItemBuilder
import dev.slne.surf.parkour.util.MessageBuilder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player

class ParkourSettingsMenu(player: Player) : ChestGui(
    5,
    ComponentHolder.of(MessageBuilder().primary("ᴇɪɴsᴛᴇʟʟᴜɴɢᴇɴ").build().decorate(TextDecoration.BOLD))
) {
    init {
        instance.launch {
            val playerData = DatabaseProvider.getPlayerData(player.uniqueId)
            val outlinePane = StaticPane(0, 0, 9, 5)
            val outlineItem = GuiItem(ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(Component.text(" ")).build())
            val menuButton =
                GuiItem(ItemBuilder(Material.BARRIER).setName(MessageBuilder().primary("Hautmenü").build())
                    .addLoreLine(MessageBuilder().info("Klicke, um zum Hautmenü zurückzukehren!").build()).build()
                ) {
                    ParkourMenu(player)
                }

            for (x in 0 until 9) {
                outlinePane.addItem(outlineItem, x, 0)
                if (x == 4) {
                    outlinePane.addItem(menuButton, x, 4)
                } else {
                    outlinePane.addItem(outlineItem, x, 4)
                }
            }

            for (y in 1 until 4) {
                outlinePane.addItem(outlineItem, 0, y)
                outlinePane.addItem(outlineItem, 8, y)
            }

            val settingsPane = StaticPane(1, 1, 7, 3)
            val currentSoundToggleState = if (playerData.likesSound) {
                MessageBuilder().success("aktiviert")
            } else {
                MessageBuilder().error("deaktiviert")
            }

            val soundSettingsItem = GuiItem(ItemBuilder(Material.JUKEBOX)
                    .setName(MessageBuilder("Sound").build())
                    .addLoreLine(MessageBuilder().info("Der Sound ist aktuell ").append(currentSoundToggleState).info(".").build())
                    .addLoreLine(MessageBuilder().info("Klicke, um die Einstellung zu ändern!").build())
                    .build()
            ) {
                playerData.edit { likesSound = !likesSound }
                if (playerData.likesSound) {
                    SurfParkour.send(
                        player,
                        MessageBuilder().primary("Die Parkour-Sounds sind nun ").success("aktiviert").primary(".")
                    )
                } else {
                    SurfParkour.send(
                        player,
                        MessageBuilder().primary("Die Parkour-Sounds sind nun ").error("deaktiviert").primary(".")
                    )
                }
                ParkourSettingsMenu(player)
            }
            settingsPane.addItem(soundSettingsItem, 0, 0)


            addPane(outlinePane)
            addPane(settingsPane)

            setOnGlobalClick { it.isCancelled = true }
            setOnGlobalDrag { it.isCancelled = true }

            show(player)
        }
    }
}