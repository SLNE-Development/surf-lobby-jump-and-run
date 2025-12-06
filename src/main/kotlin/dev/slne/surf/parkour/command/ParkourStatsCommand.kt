package dev.slne.surf.parkour.command

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.parkour.permission.ParkourPermissionRegistry
import dev.slne.surf.parkour.plugin
import dev.slne.surf.parkour.service.parkourService
import dev.slne.surf.parkour.util.appendLinePrefix
import dev.slne.surf.parkour.util.formattedDuration
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import net.kyori.adventure.text.format.TextDecoration

fun CommandAPICommand.parkourStatsCommand() = subcommand("stats") {
    withPermission(ParkourPermissionRegistry.COMMAND_PARKOUR_STATS)
    playerExecutor { player, _ ->
        plugin.launch {
            val stats = parkourService.getRuns(player.uniqueId)

            val averageTime = if (stats.isNotEmpty()) {
                val totalTime = stats.sumOf { it.time }
                totalTime / stats.size
            } else {
                0L
            }

            val averageJumps = if (stats.isNotEmpty()) {
                val totalJumps = stats.sumOf { it.jumps }
                totalJumps / stats.size
            } else {
                0
            }

            val jumpHighscore = stats.maxByOrNull { it.jumps }?.jumps ?: 0
            val timeHighscore = stats.maxByOrNull { it.time }?.time ?: 0L

            player.sendText {
                primary("Deine Parkour-Statistiken".toSmallCaps(), TextDecoration.BOLD)
                appendNewline()

                appendNewline {
                    appendLinePrefix()
                    variableKey("Durchschnittliche Zeit:")
                    appendSpace()
                    variableValue(averageTime.formattedDuration)
                }

                appendNewline {
                    appendLinePrefix()
                    variableKey("Durchschnittliche Sprünge:")
                    appendSpace()
                    variableValue(averageJumps)
                    spacer(" Sprünge")
                }

                appendNewline()

                appendNewline {
                    appendLinePrefix()
                    variableKey("Bestzeit:")
                    appendSpace()
                    variableValue(timeHighscore.formattedDuration)
                }

                appendNewline {
                    appendLinePrefix()
                    variableKey("Höchste Sprunganzahl:")
                    appendSpace()
                    variableValue(jumpHighscore)
                    spacer(" Sprünge")
                }
            }
        }
    }
}