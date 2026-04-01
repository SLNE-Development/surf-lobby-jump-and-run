package dev.slne.surf.parkour.paper.menu.view

import dev.slne.surf.parkour.paper.menu.util.*
import dev.slne.surf.parkour.paper.service.ParkourService
import dev.slne.surf.surfapi.bukkit.api.builder.buildItem
import dev.slne.surf.surfapi.bukkit.api.builder.buildLore
import dev.slne.surf.surfapi.bukkit.api.builder.displayName
import dev.slne.surf.surfapi.bukkit.api.inventory.framework.titleBuilder
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import me.devnatan.inventoryframework.View
import me.devnatan.inventoryframework.ViewConfigBuilder
import me.devnatan.inventoryframework.context.RenderContext
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.meta.SkullMeta
import java.util.*

@Suppress("UnstableApiUsage")
object ParkourActivePlayersView : View() {
    private val paginationState = buildLazyPaginationState<Pair<Int, UUID>> { _ ->
        getActivePlayerStats().toMutableList()
    }.elementFactory { _, builder, _, pair ->
        builder.withItem(createActivePlayerItem(pair.first, pair.second)).onClick { context ->
            context.playGeneralClickSound()
        }
    }.layoutTarget('R').build()

    override fun onInit(config: ViewConfigBuilder) {
        config
            .titleBuilder {
                parkourColored("Parkourstatistiken".toSmallCaps(), TextDecoration.BOLD)
            }
            .size(6)
            .layout(
                "OOOOOOOOO",
                "ORRRRRRRO",
                "ORRRRRRRO",
                "ORRRRRRRO",
                "ORRRRRRRO",
                "OOOPBNOOO"
            )
            .cancelInteractions()
    }

    override fun onFirstRender(render: RenderContext) {
        val pagination = paginationState.get(render)

        render.layoutSlot('O', outlineItem)
        render.layoutSlot('B', backItem).onClick { context ->
            context.openForPlayer(ParkourOverviewView::class.java)
        }
        render
            .layoutSlot('P')
            .renderWith {
                previousItem
            }
            .watch(paginationState)
            .onClick { context ->
                context.playNewPageSound()
                pagination.back()
            }

        render
            .layoutSlot('N')
            .renderWith {
                nextItem
            }
            .watch(paginationState)
            .onClick { context ->
                context.playNewPageSound()
                pagination.advance()
            }
    }
}

fun createActivePlayerItem(currentJumps: Int, playerUuid: UUID) = buildItem(Material.PLAYER_HEAD) {
    editMeta(SkullMeta::class.java) {
        it.owningPlayer = Bukkit.getPlayer(playerUuid)
    }

    displayName {
        parkourColored(Bukkit.getPlayer(playerUuid)?.name ?: "#Unbekannt", TextDecoration.BOLD)
    }

    buildLore {
        emptyLine()
        line {
            parkourColored("Aktueller Lauf".toSmallCaps(), TextDecoration.BOLD)
        }
        line {
            spacer("-")
            appendSpace()
            parkourColored("Sprünge: ")
            variableValue(currentJumps)
        }
    }
}

private fun getActivePlayerStats(): List<Pair<Int, UUID>> {
    val parkour = ParkourService.parkours.firstOrNull() ?: return emptyList()

    return parkour.players.map {
        Pair(parkour.getCurrentIndex(it), it)
    }
}