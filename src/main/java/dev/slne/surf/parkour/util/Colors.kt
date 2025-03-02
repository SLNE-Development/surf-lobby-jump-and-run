package dev.slne.surf.parkour.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor

object Colors {
    val PRIMARY: TextColor = TextColor.color(0x3b92d1)
    val SECONDARY: TextColor = TextColor.color(0x5b5b5b)
    val INFO: TextColor = TextColor.color(0x40d1db)
    val SUCCESS: TextColor = TextColor.color(0x65ff64)
    val WARNING: TextColor = TextColor.color(0xf9c353)
    val ERROR: TextColor = TextColor.color(0xee3d51)
    val VARIABLE_KEY: TextColor = INFO
    val VARIABLE_VALUE: TextColor = WARNING
    val SPACER: NamedTextColor = NamedTextColor.GRAY
    val DARK_SPACER: NamedTextColor = NamedTextColor.DARK_GRAY
    val PREFIX_COLOR: TextColor = PRIMARY

    val PREFIX: Component = Component.text(">> ", DARK_SPACER)
        .append(Component.text("SP", PREFIX_COLOR))
        .append(Component.text(" | ", DARK_SPACER))

    val BLACK: NamedTextColor = NamedTextColor.BLACK
    val DARK_BLUE: NamedTextColor = NamedTextColor.DARK_BLUE
    val DARK_GREEN: NamedTextColor = NamedTextColor.DARK_GREEN
    val DARK_AQUA: NamedTextColor = NamedTextColor.DARK_AQUA
    val DARK_RED: NamedTextColor = NamedTextColor.DARK_RED
    val DARK_PURPLE: NamedTextColor = NamedTextColor.DARK_PURPLE
    val GOLD: NamedTextColor = NamedTextColor.GOLD
    val GRAY: NamedTextColor = NamedTextColor.GRAY
    val DARK_GRAY: NamedTextColor = NamedTextColor.DARK_GRAY
    val BLUE: NamedTextColor = NamedTextColor.BLUE
    val GREEN: NamedTextColor = NamedTextColor.GREEN
    val AQUA: NamedTextColor = NamedTextColor.AQUA
    val RED: NamedTextColor = NamedTextColor.RED
    val LIGHT_PURPLE: NamedTextColor = NamedTextColor.LIGHT_PURPLE
    val YELLOW: NamedTextColor = NamedTextColor.YELLOW
    val WHITE: NamedTextColor = NamedTextColor.WHITE
}