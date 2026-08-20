package dev.slne.surf.parkour.minestom.menu.util

import dev.slne.surf.api.minestom.builder.buildItem
import dev.slne.surf.parkour.core.client.menu.ParkourMenuContent
import dev.slne.surf.parkour.core.client.menu.ParkourMenuTextures
import me.devnatan.inventoryframework.View
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material

val View.outlineItem: ItemStack
    get() = buildItem(Material.GRAY_STAINED_GLASS_PANE) {
        displayName {
            spacer("")
        }
    }

val previousItem = playerHeadOf(ParkourMenuTextures.ARROW_LEFT)
    .named(ParkourMenuContent.previousPageName)

val nextItem = playerHeadOf(ParkourMenuTextures.ARROW_RIGHT)
    .named(ParkourMenuContent.nextPageName)

val backItem = playerHeadOf(ParkourMenuTextures.CROSS)
    .named(ParkourMenuContent.backName)
