package dev.slne.surf.parkour.minestom.menu.view

import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.minestom.inventory.framework.titleBuilder
import dev.slne.surf.parkour.core.client.menu.ParkourMenuContent
import dev.slne.surf.parkour.core.client.menu.ParkourMenuTitles
import dev.slne.surf.parkour.core.client.message.parkourColored
import dev.slne.surf.parkour.core.client.platform.ParkourPlatform
import dev.slne.surf.parkour.core.client.service.ParkourService
import dev.slne.surf.parkour.minestom.menu.util.backItem
import dev.slne.surf.parkour.minestom.menu.util.named
import dev.slne.surf.parkour.minestom.menu.util.nextItem
import dev.slne.surf.parkour.minestom.menu.util.outlineItem
import dev.slne.surf.parkour.minestom.menu.util.playGeneralClickSound
import dev.slne.surf.parkour.minestom.menu.util.playNewPageSound
import dev.slne.surf.parkour.minestom.menu.util.playerHead
import dev.slne.surf.parkour.minestom.menu.util.previousItem
import me.devnatan.inventoryframework.View
import me.devnatan.inventoryframework.ViewConfigBuilder
import me.devnatan.inventoryframework.context.RenderContext
import net.kyori.adventure.text.format.TextDecoration
import java.util.*

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
                parkourColored(ParkourMenuTitles.STATISTICS.toSmallCaps(), TextDecoration.BOLD)
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

fun createActivePlayerItem(currentJumps: Int, playerUuid: UUID) = playerUuid.playerHead().named(
    ParkourMenuContent.activePlayerName(ParkourPlatform.playerName(playerUuid)),
    ParkourMenuContent.activePlayerLore(currentJumps)
)

private fun getActivePlayerStats(): List<Pair<Int, UUID>> {
    val parkour = ParkourService.parkours.firstOrNull() ?: return emptyList()

    return parkour.players.map {
        Pair(parkour.getCurrentIndex(it), it)
    }
}
