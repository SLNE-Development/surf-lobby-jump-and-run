package dev.slne.surf.parkour.paper.command

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.parkour.core.common.service.parkourRunsService
import dev.slne.surf.parkour.paper.permission.ParkourPermissionRegistry
import dev.slne.surf.parkour.paper.plugin
import dev.slne.surf.parkour.paper.util.appendLinePrefix
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import net.kyori.adventure.text.format.TextDecoration

fun CommandAPICommand.parkourStatsCommand() = subcommand("stats") {
    withPermission(ParkourPermissionRegistry.COMMAND_PARKOUR_STATS)
    playerExecutor { player, _ ->
        plugin.launch {
            val stats = parkourRunsService.getStats(player.uniqueId)

            val averageJumps = stats.totalJumps / stats.totalRuns

            player.sendText {
                appendNewline()
                primary("Deine Parkour-Statistiken".toSmallCaps(), TextDecoration.BOLD)
                appendNewline()

                appendNewline {
                    appendLinePrefix()
                    variableKey("Gesamte Läufe:")
                    appendSpace()
                    variableValue(stats.totalRuns)
                }

                appendNewline {
                    appendLinePrefix()
                    variableKey("Gesamte Sprünge:")
                    appendSpace()
                    variableValue(stats.totalJumps)
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
                    variableKey("Höchste Sprunganzahl:")
                    appendSpace()
                    variableValue(stats.highscore)
                    spacer(" Sprünge")
                }
            }
        }
    }
}