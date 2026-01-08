package dev.slne.surf.parkour.util

import dev.slne.surf.surfapi.bukkit.api.builder.buildLore
import dev.slne.surf.surfapi.bukkit.api.builder.displayName
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Color
import org.bukkit.inventory.ItemType
import org.bukkit.inventory.meta.ColorableArmorMeta

fun SurfComponentBuilder.white(text: String) = text(text, NamedTextColor.WHITE)

val inventoryItem
    get() = ItemType.LEATHER_BOOTS.createItemStack().apply {
        editMeta(ColorableArmorMeta::class.java) {
            it.setColor(Color.fromRGB(3, 252, 198))
        }

        displayName {
            localColored("Parkour")
        }

        buildLore {
            emptyLine()
            line {
                variableValue("Beschreibung:".toSmallCaps())
            }
            line {
                spacer("-")
                appendSpace()
                localColored("Starte den Lobby Parkour")
            }

            line {
                spacer("-")
                appendSpace()
                localColored("Siehe Statistiken an")
            }

            line {
                spacer("-")
                appendSpace()
                localColored("Stelle neue Rekorde auf")
            }
            emptyLine()

            line {
                spacer("» Klicke, um das Parkour Menu zu öffnen")
            }
        }
    }

private fun SurfComponentBuilder.localColored(text: Any, vararg decoration: TextDecoration) =
    text(text.toString(), TextColor.fromHexString("#03fcc6"), *decoration)