package dev.slne.surf.parkour.util

import net.kyori.adventure.text.format.TextColor

object PluginColor {
    val BLUE_DARK: TextColor = TextColor.fromHexString("#3b92d1") ?: TextColor.color(0, 0, 0)
    val BLUE: TextColor = TextColor.fromHexString("#40d1db") ?: TextColor.color(0, 0, 0)
    val BLUE_LIGHT: TextColor = TextColor.fromHexString("#96cfe7") ?: TextColor.color(0, 0, 0)
    val BLUE_MID: TextColor = TextColor.fromHexString("#4bb8f8") ?: TextColor.color(0, 0, 0)
    val RED: TextColor = TextColor.fromHexString("#e92a25") ?: TextColor.color(0, 0, 0)
    val GOLD: TextColor = TextColor.fromHexString("#fba24e") ?: TextColor.color(0, 0, 0)
    val LIGHT_GRAY: TextColor = TextColor.fromHexString("#d3d3d3") ?: TextColor.color(0, 0, 0)
    val DARK_GRAY: TextColor = TextColor.fromHexString("#a9a9a9") ?: TextColor.color(0, 0, 0)
    val WHITE: TextColor = TextColor.fromHexString("#ffffff") ?: TextColor.color(0, 0, 0)
}
