package dev.slne.surf.lobby.jar.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

val prefix: Component = Component.text(">> ", NamedTextColor.GRAY)
    .append(Component.text("Parkour", PluginColor.BLUE_LIGHT))
    .append(Component.text(" | ", NamedTextColor.DARK_GRAY))