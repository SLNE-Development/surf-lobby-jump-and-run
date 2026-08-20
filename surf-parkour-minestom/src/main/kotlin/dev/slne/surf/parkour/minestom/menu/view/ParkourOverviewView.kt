package dev.slne.surf.parkour.minestom.menu.view

import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.minestom.builder.buildItem
import dev.slne.surf.api.minestom.inventory.framework.titleBuilder
import dev.slne.surf.parkour.core.client.command.appendNoParkoursAvailable
import dev.slne.surf.parkour.core.client.menu.ParkourMenuContent
import dev.slne.surf.parkour.core.client.menu.ParkourMenuTextures
import dev.slne.surf.parkour.core.client.menu.ParkourMenuTitles
import dev.slne.surf.parkour.core.client.message.parkourColored
import dev.slne.surf.parkour.core.client.platform.ParkourPlatform
import dev.slne.surf.parkour.core.client.service.ParkourRunsService
import dev.slne.surf.parkour.core.client.service.ParkourService
import dev.slne.surf.parkour.minestom.menu.util.named
import dev.slne.surf.parkour.minestom.menu.util.outlineItem
import dev.slne.surf.parkour.minestom.menu.util.playGeneralClickSound
import dev.slne.surf.parkour.minestom.menu.util.playerHead
import dev.slne.surf.parkour.minestom.menu.util.playerHeadOf
import me.devnatan.inventoryframework.View
import me.devnatan.inventoryframework.ViewConfigBuilder
import me.devnatan.inventoryframework.context.RenderContext
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.item.Material

object ParkourOverviewView : View() {
    override fun onInit(config: ViewConfigBuilder) {
        config
            .titleBuilder {
                parkourColored(ParkourMenuTitles.OVERVIEW.toSmallCaps(), TextDecoration.BOLD)
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
            val parkour = ParkourService.parkours.firstOrNull() ?: run {
                context.player.sendText { appendNoParkoursAvailable() }
                return@onClick
            }

            context.closeForPlayer()

            ParkourPlatform.launch {
                parkour.start(context.player.uuid)
            }
        }
        render.layoutSlot('A', activeItem).onClick { context ->
            context.playGeneralClickSound()
            context.openForPlayer(ParkourActivePlayersView::class.java)
        }
        render.layoutSlot('M', ownItem(render))
    }
}

private fun ownItem(render: RenderContext) = render.player.uuid.playerHead().named(
    ParkourMenuContent.ownStatsName,
    ParkourMenuContent.ownStatsLore(ParkourRunsService.getStats(render.player.uuid))
)

private val closeItem = playerHeadOf(ParkourMenuTextures.CROSS)
    .named(ParkourMenuContent.closeName)

private val leaderBoardItem = buildItem(Material.NETHER_STAR)
    .named(ParkourMenuContent.leaderboardName, ParkourMenuContent.leaderboardLore)

private val startItem = buildItem(Material.RECOVERY_COMPASS)
    .named(ParkourMenuContent.startName)

private val activeItem = buildItem(Material.WRITABLE_BOOK)
    .named(ParkourMenuContent.activePlayersName)
