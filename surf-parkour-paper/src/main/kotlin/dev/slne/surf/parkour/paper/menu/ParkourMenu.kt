package dev.slne.surf.parkour.paper.menu

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder
import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import dev.slne.surf.parkour.paper.menu.submenu.ParkourActivePlayersMenu
import dev.slne.surf.parkour.paper.menu.submenu.ParkourGeneralFailureMenu
import dev.slne.surf.parkour.paper.menu.submenu.ParkourScoreboardMenu
import dev.slne.surf.parkour.paper.menu.submenu.ParkourSelectMenu
import dev.slne.surf.parkour.paper.menu.type.LeaderboardSortingType
import dev.slne.surf.parkour.paper.menu.type.RedirectType
import dev.slne.surf.parkour.paper.menu.util.*
import dev.slne.surf.parkour.paper.model.parkour.PersonalParkourSummary
import dev.slne.surf.parkour.paper.plugin
import dev.slne.surf.parkour.paper.service.parkourService
import dev.slne.surf.surfapi.bukkit.api.builder.buildItem
import dev.slne.surf.surfapi.bukkit.api.builder.buildLore
import dev.slne.surf.surfapi.bukkit.api.builder.displayName
import dev.slne.surf.surfapi.bukkit.api.builder.lore
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import dev.slne.surf.surfapi.core.api.util.int2ObjectMapOf
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class ParkourMenu(override val statistics: PersonalParkourSummary) : PlayerDataHolderGui {
    companion object {
        private val closeMenuItem = GuiItem(buildItem(Material.BARRIER) {
            displayName { primary("Schließen") }
            lore { info("Klicke, um das Hautmenü zu schließen!") }
        }) { it.whoClicked.closeInventory() }
    }

    private val statsItem = GuiItem(buildItem(Material.NETHER_STAR) {
        displayName { primary("Bestenliste") }
        lore { info("Klicke, um dir die Bestenliste anzusehen!") }
    }) { it.handleStatsItem() }

    private val startItem = GuiItem(buildItem(Material.RECOVERY_COMPASS) {
        displayName { primary("Parkour starten") }
        lore { info("Klicke, um einen Parkour zu starten!") }
    }) { it.handleStart() }

    private val activePlayersItem = GuiItem(buildItem(Material.WRITABLE_BOOK) {
        displayName { primary("Aktive Spieler") }
        lore { info("Klicke, um dir die aktiven Spieler anzusehen!") }
    }) { it.handleActivePlayers() }

    suspend fun open(player: Player) {
        val gui = ChestGui(5, ComponentHolder.of(buildText {
            primary("Parkour".toSmallCaps(), TextDecoration.BOLD)

            if (plugin.parkourConfig.config.betaMode) {
                error(" ʙᴇᴛᴀ", TextDecoration.BOLD)
            }
        }))

        gui.cancelGlobalDrag()
        gui.cancelGlobalClick()

        val outlineItem = gui.outlineItem()
        val outlinePane = StaticPane(0, 0, 9, 5).apply {
            fillTopAndBottomRows(
                outlineItem,
                bottomCustom = int2ObjectMapOf(4 to closeMenuItem)
            )
            fillLeftRightColumns(outlineItem)
        }

        val playerHeadPane = StaticPane(4, 1, 1, 1).apply {
            val profileHead = HeadUtil.getPlayerHead(statistics.uuid).apply {
                displayName(text(statistics.name))
                buildLore {
                    line {
                        spacer(" - ")
                        variableKey("Sprünge: ".toSmallCaps())
                        variableValue(statistics.totalJumps.toString())
                    }
                    line {
                        spacer(" - ")
                        variableKey("Versuche: ".toSmallCaps())
                        variableValue(statistics.totalTries.toString())
                    }
                    line {
                        spacer(" - ")
                        variableKey("Highscore: ".toSmallCaps())
                        variableValue(statistics.bestJumps.toString())
                    }
                }

            }
            addItem(GuiItem(profileHead), 0, 0)
        }


        val taskbarPane = StaticPane(0, 3, 9, 1).apply {
            addItem(statsItem, 2, 0)
            addItem(startItem, 4, 0)
            addItem(activePlayersItem, 6, 0)
        }

        gui.addPane(taskbarPane)
        gui.addPane(outlinePane)
        gui.addPane(playerHeadPane)

        gui.show(player)
    }

    private fun InventoryClickEvent.handleStart() {
        plugin.launch {
            val parkours = parkourService.getParkours()

            if (parkourService.isInParkour(player.uniqueId)) {
                withContext(plugin.entityDispatcher(player)) {
                    player.closeInventory()
                }

                player.sendText {
                    appendPrefix()
                    error("Du bist bereits in einem Parkour!")
                }
                return@launch
            }

            when {
                parkours.isEmpty() -> withContext(plugin.entityDispatcher(player)) {
                    ParkourGeneralFailureMenu(
                        statistics,
                        buildText { error("Es gibt keine verfügbaren Parkours!") }
                    ).show(whoClicked)
                }


                parkours.size == 1 -> parkours.first().start(player.uniqueId)
                    .also {
                        withContext(plugin.entityDispatcher(player)) {
                            player.closeInventory()
                        }
                    }

                else -> withContext(plugin.entityDispatcher(player)) {
                    ParkourSelectMenu(statistics, RedirectType.START_PARKOUR).show(whoClicked)
                }
            }
        }
    }

    private fun InventoryClickEvent.handleActivePlayers() {
        val parkours = parkourService.getParkours()

        when {
            parkours.isEmpty() -> ParkourGeneralFailureMenu(
                statistics,
                buildText { error("Es gibt keine verfügbaren Parkours!") }
            ).show(whoClicked)

            parkours.size == 1 -> {
                val parkour = parkours.first()

                if (parkour.players.isEmpty()) {
                    ParkourGeneralFailureMenu(
                        statistics,
                        buildText { error("Dieser Parkour ist leer!") }
                    ).show(whoClicked)
                } else {
                    plugin.launch(plugin.entityDispatcher(player)) {
                        ParkourActivePlayersMenu(parkour, statistics).open(player)
                    }
                }
            }


            else -> ParkourSelectMenu(statistics, RedirectType.PARKOUR_ACTIVES).show(whoClicked)
        }
    }


    private fun InventoryClickEvent.handleStatsItem() {
        ParkourScoreboardMenu(statistics, LeaderboardSortingType.POINTS_HIGHEST).show(whoClicked)
    }
}
