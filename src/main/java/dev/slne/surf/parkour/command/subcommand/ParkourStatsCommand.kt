package dev.slne.surf.parkour.command.subcommand

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.entitySelectorArgumentOnePlayer
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.parkour.database.DatabaseProvider
import dev.slne.surf.parkour.parkour.Parkour
import dev.slne.surf.parkour.plugin
import dev.slne.surf.parkour.util.Permission
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

class ParkourStatsCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(Permission.COMMAND_PARKOUR_STATISTIC)

        entitySelectorArgumentOnePlayer("target", optional = true)

        playerExecutor { player, args ->
            val target = args.getOrDefaultUnchecked("target", player)

            plugin.launch {
                val playerData = DatabaseProvider.getPlayerData(target.uniqueId)
                val parkour = Parkour.getParkour(target)

                if (parkour == null) {
                    player.sendMessage(
                        createStatisticMessage(
                            playerData.points,
                            playerData.highScore,
                            if (player == target) "Du spielst aktuell keinen Parkour." else "${target.name} spielt aktuell keinen Parkour.",
                            playerData.trys
                        )
                    )
                } else {
                    player.sendMessage(
                        createStatisticMessage(
                            playerData.points,
                            playerData.highScore,
                            parkour.currentPoints.getInt(target.uniqueId).toString(),
                            playerData.trys
                        )
                    )
                }
            }
        }
    }

    companion object {
        fun createStatisticMessage(
            points: Int,
            highScore: Int,
            current: String,
            trys: Int
        ) = buildText {
            appendNewPrefixedLine()
            append {
                spacer("---------------- ")
                primary("Statistik")
                spacer(" ----------------").build()
            }
            appendNewPrefixedLine()
            appendNewPrefixedLine()
            append {
                primary("Seit Aufzeichnung:")
                appendNewPrefixedLine()
                appendNewPrefixedLine()
                append {
                    spacer(" - ")
                    variableKey("Sprünge: ")
                    variableValue(points.toString()).build()
                }
                appendNewPrefixedLine()
                append {
                    spacer(" - ")
                    variableKey("Rekord: ")
                    variableValue(highScore.toString()).build()
                }

                appendNewPrefixedLine()
                append {
                    spacer(" - ").build()
                    variableKey("Versuche: ").build()
                    variableValue(trys.toString()).build()
                }.build()
            }
            appendNewPrefixedLine()
            appendNewPrefixedLine()
            append {
                primary("Aktueller Parkour:")
                appendNewPrefixedLine()
                appendNewPrefixedLine()
                append {
                    spacer(" - ")
                    variableKey("Sprünge: ")
                    variableValue(current).build()
                }.build()
            }
            appendNewPrefixedLine()
            appendNewPrefixedLine()
            spacer("----------------------------------------")
        }
    }
}
