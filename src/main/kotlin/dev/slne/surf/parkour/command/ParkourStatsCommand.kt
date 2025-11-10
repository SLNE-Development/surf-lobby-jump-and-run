package dev.slne.surf.parkour.command

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.parkour.`object`.parkour.ParkourRun
import dev.slne.surf.parkour.permission.ParkourPermissionRegistry
import dev.slne.surf.parkour.plugin
import dev.slne.surf.parkour.service.parkourService
import dev.slne.surf.parkour.util.formattedTimeEpoch
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.pagination.Pagination
import net.kyori.adventure.text.format.TextDecoration

fun CommandAPICommand.parkourStatsCommand() = subcommand("stats") {
    withPermission(ParkourPermissionRegistry.COMMAND_PARKOUR_STATS)
    playerExecutor { player, _ ->
        plugin.launch {
            val stats = parkourService.getRuns(player)

            Pagination<ParkourRun> {
                title { primary("Parkour-Statistiken".toSmallCaps(), TextDecoration.BOLD) }
                rowRenderer { row, _ ->
                    listOf(
                        buildText {
                            darkSpacer(">")
                            appendSpace()
                            variableKey(row.parkour.displayName)
                            spacer(":")
                            appendSpace()
                            variableValue(row.jumps)
                            spacer(" in ")
                            variableValue(row.time.formattedTimeEpoch)
                        }
                    )
                }
            }

            player.sendText {
                append(Pagination<ParkourRun> {
                    title { primary("Parkour-Statistiken".toSmallCaps(), TextDecoration.BOLD) }
                    rowRenderer { row, _ ->
                        listOf(
                            buildText {
                                darkSpacer(">")
                                appendSpace()
                                variableKey(row.parkour.displayName)
                                spacer(":")
                                appendSpace()
                                variableValue(row.jumps)
                                spacer(" in ")
                                variableValue(row.time.formattedTimeEpoch)
                            }
                        )
                    }
                }.renderComponent(stats))
            }
        }
    }
}