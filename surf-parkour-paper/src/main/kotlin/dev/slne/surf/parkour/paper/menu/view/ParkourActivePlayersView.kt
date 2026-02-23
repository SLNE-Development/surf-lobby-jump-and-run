package dev.slne.surf.parkour.paper.menu.view

import dev.slne.surf.parkour.paper.menu.util.*
import dev.slne.surf.parkour.paper.service.parkourService
import dev.slne.surf.surfapi.bukkit.api.builder.buildItem
import dev.slne.surf.surfapi.bukkit.api.builder.buildLore
import dev.slne.surf.surfapi.bukkit.api.inventory.framework.titleBuilder
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import me.devnatan.inventoryframework.View
import me.devnatan.inventoryframework.ViewConfigBuilder
import me.devnatan.inventoryframework.context.Context
import me.devnatan.inventoryframework.context.RenderContext
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.inventory.meta.SkullMeta
import java.util.*

@Suppress("UnstableApiUsage")
object ParkourActivePlayersView : View() {
    private val paginationState = buildComputedPaginationState<Pair<Int, UUID>> { _ ->
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
            .displayIf { context -> paginationState.get(context).canBack() }
            .onClick { context ->
                context.playNewPageSound()
                paginationState.get(context).back()
            }

        render
            .layoutSlot('N')
            .renderWith {
                nextItem
            }
            .watch(paginationState)
            .displayIf { context -> paginationState.get(context).canAdvance() }
            .onClick { context ->
                context.playNewPageSound()
                paginationState.get(context).advance()
            }
    }

    override fun onUpdate(update: Context) {
        updatePagination(update)
    }

    override fun onResume(origin: Context, target: Context) {
        target.update()
    }

    private fun updatePagination(context: Context) {
        val pagination = paginationState.get(context)
        pagination.switchTo(pagination.currentPageIndex())
    }
}

fun createActivePlayerItem(currentJumps: Int, playerUuid: UUID) = buildItem(Material.PLAYER_HEAD) {
    editMeta(SkullMeta::class.java) {
        // TODO: Player Skin
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
    val parkour = parkourService.getParkours().firstOrNull() ?: return emptyList()
    return parkour.players.map {
        Pair(parkour.getCurrentIndex(it), it)
    }
}