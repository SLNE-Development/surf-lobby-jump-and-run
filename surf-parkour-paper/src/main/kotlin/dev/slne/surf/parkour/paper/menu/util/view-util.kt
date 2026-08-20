package dev.slne.surf.parkour.paper.menu.util

import dev.slne.surf.api.core.messages.adventure.playSound
import dev.slne.surf.api.paper.builder.buildItem
import dev.slne.surf.api.paper.builder.displayName
import dev.slne.surf.parkour.core.client.menu.ParkourMenuContent
import dev.slne.surf.parkour.core.client.menu.ParkourMenuTextures
import dev.slne.surf.parkour.paper.util.playerHeadOf
import me.devnatan.inventoryframework.View
import me.devnatan.inventoryframework.context.Context
import me.devnatan.inventoryframework.context.SlotClickContext
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.inventory.ItemStack

val View.outlineItem: ItemStack
    get() = buildItem(Material.GRAY_STAINED_GLASS_PANE) {
        displayName {
            spacer("")
        }
    }

fun Context.playGeneralClickSound() {
    player.playSound(true) {
        type(Sound.UI_BUTTON_CLICK)
    }
}

fun Context.playNewPageSound() {
    player.playSound(true) {
        type(Sound.ENTITY_CHICKEN_EGG)
    }
}

fun SlotClickContext.playGeneralClickSound() {
    player.playSound(true) { type(Sound.UI_BUTTON_CLICK) }
}

fun SlotClickContext.playNewPageSound() {
    player.playSound(true) { type(Sound.ENTITY_CHICKEN_EGG) }
}

val previousItem = playerHeadOf(ParkourMenuTextures.ARROW_LEFT).apply {
    displayName(ParkourMenuContent.previousPageName)
}

val nextItem = playerHeadOf(ParkourMenuTextures.ARROW_RIGHT).apply {
    displayName(ParkourMenuContent.nextPageName)
}

val backItem = playerHeadOf(ParkourMenuTextures.CROSS).apply {
    displayName(ParkourMenuContent.backName)
}
