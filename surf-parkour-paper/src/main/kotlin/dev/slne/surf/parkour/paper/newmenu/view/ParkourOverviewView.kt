package dev.slne.surf.parkour.paper.newmenu.view

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.parkour.paper.newmenu.util.MenuHeads
import dev.slne.surf.parkour.paper.newmenu.util.auctionColored
import dev.slne.surf.parkour.paper.newmenu.util.outlineItem
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
                auctionColored("Parkour".toSmallCaps(), TextDecoration.BOLD)
            }
            .size(5)
            .layout(
                "OOOOOOOOO",
                "ORRRMRRRO",
                "ORRRRRRRO",
                "ORLRSRARO",
                "OOOOCOOOO"
            )
            .cancelInteractions()
    }

    override fun onFirstRender(render: RenderContext) {
        render.layoutSlot('O', outlineItem)
        render.layoutSlot('C', closeItem).onClick { context ->
            context.closeForPlayer()
        }
        render.layoutSlot('L', leaderBoardItem).onClick { context ->
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
            spacer("-")
            appendSpace()
            auctionColored("Sprünge: ")
            variableValue(parkourService.getStats(render.player.uniqueId))
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