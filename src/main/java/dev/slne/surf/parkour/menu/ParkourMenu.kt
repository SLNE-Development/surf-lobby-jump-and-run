package dev.slne.surf.parkour.menu

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import dev.slne.surf.parkour.database.DatabaseProvider
import dev.slne.surf.parkour.leaderboard.LeaderboardSortingType
import dev.slne.surf.parkour.menu.submenu.*
import dev.slne.surf.parkour.menu.type.RedirectType
import dev.slne.surf.parkour.menu.util.fillLeftRightColumns
import dev.slne.surf.parkour.menu.util.fillTopAndBottomRows
import dev.slne.surf.parkour.menu.util.outlineItem
import dev.slne.surf.parkour.menu.util.player
import dev.slne.surf.parkour.player.PlayerData
import dev.slne.surf.parkour.plugin
import dev.slne.surf.surfapi.bukkit.api.builder.*
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import dev.slne.surf.surfapi.core.api.util.int2ObjectMapOf
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.meta.SkullMeta

class ParkourMenu(playerData: PlayerData) : AbstractParkourGui(5, buildText {
    primary("Parkour".toSmallCaps())
    if (plugin.betaMode) {
        error(" ʙᴇᴛᴀ")
    }
    decorate(TextDecoration.BOLD)
}, playerData) {
    companion object {
        private val closeMenuItem = GuiItem(buildItem(Material.BARRIER) {
            displayName { primary("Schließen") }
            lore { info("Klicke, um das Hautmenü zu schließen!") }
        }) { it.whoClicked.closeInventory() }

        suspend operator fun invoke(player: Player): ParkourMenu {
            val data = DatabaseProvider.getPlayerData(player.uniqueId)
            return ParkourMenu(data)
        }

        fun lazyOpen(player: Player) {
            plugin.launch {
                val menu = invoke(player)
                withContext(plugin.entityDispatcher(player)) {
                    menu.show(player)
                }
            }
        }
    }

    private val statsItem = GuiItem(buildItem(Material.NETHER_STAR) {
        displayName { primary("Bestenliste") }
        lore { info("Klicke, um dir die Bestenliste anzusehen!") }
    }) { it.handleStatsItem() }

    private val startItem = GuiItem(buildItem(Material.RECOVERY_COMPASS) {
        displayName { primary("Parkour starten") }
        lore { info("Klicke, um einen Parkour zu starten!") }
    }) { it.handleStart() }

    private val settingsItem = GuiItem(buildItem(Material.REPEATING_COMMAND_BLOCK) {
        displayName { primary("Einstellungen") }
        lore { info("Klicke, um zu den Einstellungen zu gelangen!") }
    }) { it.handleSettings() }

    private val activePlayersItem = GuiItem(buildItem(Material.WRITABLE_BOOK) {
        displayName { primary("Aktive Spieler") }
        lore { info("Klicke, um dir die aktiven Spieler anzusehen!") }
    }) { it.handleActivePlayers() }

    init {
        val (uuid, name, highscore, jumps, tries) = playerData

        val outlineItem = outlineItem()
        val outlinePane = StaticPane(0, 0, 9, 5).apply {
            fillTopAndBottomRows(outlineItem, bottomCustom = int2ObjectMapOf(4 to closeMenuItem))
            fillLeftRightColumns(outlineItem)
        }

        val playerHeadPane = StaticPane(4, 1, 1, 1).apply {
            val profileHead = buildItem(Material.PLAYER_HEAD) {
                displayName(text(name))
                buildLore {
                    line {
                        spacer(" - ")
                        variableKey("Sprünge: ".toSmallCaps())
                        variableValue(jumps.toString())
                    }
                    line {
                        spacer(" - ")
                        variableKey("Versuche: ".toSmallCaps())
                        variableValue(tries.toString())
                    }
                    line {
                        spacer(" - ")
                        variableKey("Highscore: ".toSmallCaps())
                        variableValue(highscore.toString())
                    }
                }

                meta<SkullMeta> { owningPlayer = Bukkit.getOfflinePlayer(uuid) }
            }

            addItem(GuiItem(profileHead), 0, 0)
        }


        val taskbarPane = StaticPane(0, 3, 9, 1).apply {
            addItem(statsItem, 1, 0)
            addItem(startItem, 3, 0)
            addItem(settingsItem, 5, 0)
            addItem(activePlayersItem, 7, 0)
        }

        addPane(taskbarPane)
        addPane(outlinePane)
        addPane(playerHeadPane)
    }

    private fun InventoryClickEvent.handleStart() {
        val parkours = DatabaseProvider.getParkours()

        when {
            parkours.isEmpty() -> ParkourGeneralFailureMenu(
                playerData,
                buildText { error("Es gibt keine verfügbaren Parkours!") }
            ).show(whoClicked)

            parkours.size == 1 -> plugin.launch { parkours.first().startParkour(player) }
            else -> ParkourSelectMenu(playerData, RedirectType.START_PARKOUR).show(whoClicked)
        }
    }

    private fun InventoryClickEvent.handleActivePlayers() {
        val parkours = DatabaseProvider.getParkours()

        when {
            parkours.isEmpty() -> ParkourGeneralFailureMenu(
                playerData,
                buildText { error("Es gibt keine verfügbaren Parkours!") }
            ).show(whoClicked)

            parkours.size == 1 -> {
                val parkour = parkours.first()

                if (parkour.activePlayers.isEmpty()) {
                    ParkourGeneralFailureMenu(
                        playerData,
                        buildText { error("Dieser Parkour ist leer!") }
                    ).show(whoClicked)
                } else {
                    ParkourActivePlayersMenu(playerData, parkour).show(whoClicked)
                }
            }


            else -> ParkourSelectMenu(playerData, RedirectType.PARKOUR_ACTIVES).show(whoClicked)
        }
    }

    private fun InventoryClickEvent.handleSettings() {
        ParkourSettingsMenu(playerData).show(whoClicked)
    }


    private fun InventoryClickEvent.handleStatsItem() {
        ParkourScoreboardMenu(playerData, LeaderboardSortingType.POINTS_HIGHEST).show(whoClicked)
    }
}
