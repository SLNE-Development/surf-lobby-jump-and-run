package dev.slne.surf.parkour.core.client.message

import dev.slne.surf.api.core.messages.builder.SurfComponentBuilder
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration

/**
 * The accent colour every parkour menu and message is written in.
 */
val parkourColor: TextColor = TextColor.color(76, 161, 127)

/**
 * Appends [text] in the parkour accent colour.
 */
fun SurfComponentBuilder.parkourColored(text: Any, vararg decoration: TextDecoration) =
    coloredComponent(text.toString(), parkourColor, *decoration)

/**
 * Appends the prefix every line of a multi-line message starts with.
 */
fun SurfComponentBuilder.appendLinePrefix() = darkSpacer("» ")
