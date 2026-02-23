package dev.slne.surf.parkour.paper.menu.view

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.parkour.paper.menu.util.MenuHeads
import dev.slne.surf.parkour.paper.menu.util.outlineItem
import dev.slne.surf.parkour.paper.menu.util.parkourColored
import dev.slne.surf.parkour.paper.menu.util.playGeneralClickSound
import dev.slne.surf.parkour.paper.plugin
import dev.slne.surf.parkour.paper.service.parkourService
import dev.slne.surf.surfapi.bukkit.api.builder.buildItem
import dev.slne.surf.surfapi.bukkit.api.builder.buildLore
import dev.slne.surf.surfapi.bukkit.api.builder.displayName
import dev.slne.surf.surfapi.bukkit.api.inventory.framework.titleBuilder
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import me.devnatan.inventoryframework.View
import me.devnatan.inventoryframework.ViewConfigBuilder
import me.devnatan.inventoryframework.context.RenderContext
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.inventory.meta.SkullMeta

object ParkourOverviewView : View() {
    override fun onInit(config: ViewConfigBuilder) {
        config
            .titleBuilder {
                parkourColored("Parkour".toSmallCaps(), TextDecoration.BOLD)
            }
            .size(5)
            .layout(
                "OOOOOOOOO",
                "O   M   O",
                "O       O",
                "O L S A O",
                "OOOOCOOOO"
            )
            .cancelInteractions()
    }

    override fun onFirstRender(render: RenderContext) {
        render.layoutSlot('O', outlineItem)
        render.layoutSlot('C', closeItem).onClick { context ->
            context.playGeneralClickSound()
            context.closeForPlayer()
        }
        render.layoutSlot('L', leaderBoardItem).onClick { context ->
            context.playGeneralClickSound()
            context.openForPlayer(ParkourLeaderboardView::class.java)
        }
        render.layoutSlot('S', startItem).onClick { context ->
            val parkour = parkourService.getParkours().firstOrNull() ?: run {
                context.player.sendText {
                    appendErrorPrefix()
                    error("Es sind derzeit keine Parkours verfügbar.")
                }
                return@onClick
            }

            plugin.launch {
                parkour.start(context.player.uniqueId)
            }
        }
        render.layoutSlot('A', activeItem).onClick { context ->
            context.playGeneralClickSound()
            context.openForPlayer(ParkourActivePlayersView::class.java)
        }
        render.layoutSlot('M', ownItem(render))
    }
}

private fun ownItem(render: RenderContext) = buildItem(Material.PLAYER_HEAD) {
    displayName {
        primary("Deine Statistiken".toSmallCaps(), TextDecoration.BOLD)
    }

    editMeta(SkullMeta::class.java) {
        it.owningPlayer = render.player
    }

    val stats = parkourService.getStats(render.player.uniqueId)

    buildLore {
        emptyLine()
        line {
            parkourColored("Parkourstatistiken".toSmallCaps(), TextDecoration.BOLD)
        }
        line {
            spacer("-")
            appendSpace()
            parkourColored("Highscore: ")
            variableValue(stats.highscore)
        }
        line {
            spacer("-")
            appendSpace()
            parkourColored("Versuche: ")
            variableValue(stats.totalRuns)
        }
        line {
            spacer("-")
            appendSpace()
            parkourColored("Gesamtsprünge: ")
            variableValue(stats.totalJumps)
        }
        line {
            spacer("-")
            appendSpace()
            parkourColored("Durchschnittliche Zeit: ")
            variableValue(stats.averageTime) // TODO: Format
        }

    }
}

private val closeItem = MenuHeads.CROSS.apply {
    displayName {
        primary("Schließen".toSmallCaps(), TextDecoration.BOLD)
    }
}

private val leaderBoardItem = buildItem(Material.NETHER_STAR) {
    displayName {
        primary("Bestenliste".toSmallCaps(), TextDecoration.BOLD)
    }
}

private val startItem = MenuHeads.CREATE_BUTTON.apply {
    displayName {
        primary("Parkour starten".toSmallCaps(), TextDecoration.BOLD)
    }
}

private val activeItem = buildItem(Material.WRITABLE_BOOK) {
    displayName {
        primary("Aktive Spieler".toSmallCaps(), TextDecoration.BOLD)
    }
}