package dev.slne.surf.parkour.paper.menu.util

import dev.slne.surf.surfapi.bukkit.api.builder.buildItem
import dev.slne.surf.surfapi.bukkit.api.builder.displayName
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.playSound
import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
import me.devnatan.inventoryframework.View
import me.devnatan.inventoryframework.context.Context
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
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

val previousItem = MenuHeads.ARROW_LEFT.clone().apply {
    displayName {
        parkourColored("Vorherige Seite")
    }
}

val nextItem = MenuHeads.ARROW_RIGHT.clone().apply {
    displayName {
        parkourColored("Nächste Seite")
    }
}

val backItem = MenuHeads.CROSS.apply {
    displayName {
        primary("Zurück".toSmallCaps(), TextDecoration.BOLD)
    }
}

fun SurfComponentBuilder.parkourColored(text: Any, vararg decoration: TextDecoration) =
    coloredComponent(text.toString(), TextColor.color(235, 177, 52), *decoration)