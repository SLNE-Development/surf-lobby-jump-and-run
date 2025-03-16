package dev.slne.surf.parkour.menu.submenu

import com.github.stefvanschie.inventoryframework.pane.StaticPane
import dev.slne.surf.parkour.menu.AbstractParkourGui
import dev.slne.surf.parkour.menu.PlayerDataHolderGui
import dev.slne.surf.parkour.menu.util.fillOuterBorder
import dev.slne.surf.parkour.menu.util.menuButton
import dev.slne.surf.parkour.menu.util.outlineItem
import dev.slne.surf.parkour.menu.util.player
import dev.slne.surf.parkour.player.PlayerData
import dev.slne.surf.parkour.send
import dev.slne.surf.surfapi.bukkit.api.builder.buildItem
import dev.slne.surf.surfapi.bukkit.api.builder.buildLore
import dev.slne.surf.surfapi.bukkit.api.builder.displayName
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent

class ParkourSettingsMenu(playerData: PlayerData) : AbstractParkourGui(5, buildText {
    primary("Einstellungen".toSmallCaps())
    decorate(TextDecoration.BOLD)
}, playerData), PlayerDataHolderGui {
    private val soundSettingsItem = updatingItem({
        buildItem(Material.JUKEBOX) {
            displayName { primary("Sound") }
            buildLore {
                line {
                    info("Der Sound ist aktuell ")
                    append {
                        if (playerData.likesSound) success("aktiviert").build() else error("deaktiviert").build()
                    }
                    info(".")
                }
                line {
                    info("Klicke, um die Einstellung zu ändern!")
                }
            }
        }
    }) { it.handleSoundSettings() }

    init {
        val outlineItem = outlineItem()
        val outlinePane = StaticPane(0, 0, 9, 5).apply {
            fillOuterBorder(outlineItem)
            addItem(menuButton(), 4, 4)
        }

        val settingsPane = StaticPane(1, 1, 7, 3).apply {
            addItem(soundSettingsItem, 0, 0)
        }

        addPane(outlinePane)
        addPane(settingsPane)
    }

    private fun InventoryClickEvent.handleSoundSettings() {
        playerData.edit { likesSound = !likesSound }
        update()
    }
}