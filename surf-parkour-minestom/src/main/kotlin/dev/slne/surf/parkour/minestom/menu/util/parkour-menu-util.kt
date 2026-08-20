package dev.slne.surf.parkour.minestom.menu.util

import dev.slne.surf.api.core.messages.adventure.playSound
import dev.slne.surf.api.minestom.builder.buildItem
import dev.slne.surf.parkour.core.client.service.ParkourTexturesService
import me.devnatan.inventoryframework.context.SlotClickContext
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.component.DataComponents
import net.minestom.server.entity.PlayerSkin
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.item.component.TooltipDisplay
import net.minestom.server.network.player.ResolvableProfile
import java.util.*

private val buttonClick = Key.key("ui.button.click")
private val chickenEgg = Key.key("entity.chicken.egg")

/**
 * The head of the player identified by this uuid, as the parkour last saw their skin.
 */
fun UUID.playerHead() = playerHeadOf(ParkourTexturesService.getTexture(this).texture)

/**
 * A player head wearing the skin [textures] encodes.
 */
fun playerHeadOf(textures: String): ItemStack = buildItem(Material.PLAYER_HEAD) {
    builder.set(DataComponents.PROFILE, ResolvableProfile(PlayerSkin(textures, null)))
    builder.set(
        DataComponents.TOOLTIP_DISPLAY,
        TooltipDisplay(false, setOf(DataComponents.PROFILE))
    )
}

/**
 * Sets the display name and lore of the item this builder produces.
 */
fun ItemStack.named(name: Component, lore: List<Component> = emptyList()): ItemStack =
    with(DataComponents.CUSTOM_NAME, name.withoutItalics())
        .with(DataComponents.LORE, lore.map { it.withoutItalics() })

private fun Component.withoutItalics() =
    decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)

fun SlotClickContext.playGeneralClickSound() {
    player.playSound(true) { type(buttonClick) }
}

fun SlotClickContext.playNewPageSound() {
    player.playSound(true) { type(chickenEgg) }
}
