package dev.slne.surf.parkour.util

import dev.slne.surf.parkour.parkourConfig
import dev.slne.surf.surfapi.bukkit.api.builder.buildItem
import dev.slne.surf.surfapi.bukkit.api.builder.buildLore
import dev.slne.surf.surfapi.bukkit.api.builder.displayName
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag

fun SurfComponentBuilder.white(text: String) = text(text, NamedTextColor.WHITE)

val inventoryItem
    get() = buildItem(Material.FIREWORK_ROCKET) {
        displayName {
            primary("Jump'n Run", TextDecoration.BOLD)
            if (parkourConfig.config.betaMode) {
                error(" Beta".toSmallCaps())
            }
        }

        addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP, ItemFlag.HIDE_ATTRIBUTES)

        buildLore {
            emptyLine()
            line {
                white("Endlich ist er da! ")
            }
            line {
                variableValue("Der Lobby Parkour.")
            }
            line {
                white("Keine Langeweile beim Warten mehr!")
            }
            emptyLine()
            line {
                white("Springe so weit wie möglich und stelle neue Rekorde auf!")
            }
            line {
                white("Klicke mit diesem Item, um ein Menu zu öffnen. Dort")
            }
            line {
                white("kannst du den Parkour starten, deine Statistiken ansehen")
            }
            line {
                white("und vieles mehr!")
            }
            if (parkourConfig.config.betaMode) {
                emptyLine()
                line {
                    spacer(
                        "Bitte beachte, das der Parkour noch in der Beta-Phase ist.",
                        TextDecoration.ITALIC
                    )
                }
            }
        }
    }