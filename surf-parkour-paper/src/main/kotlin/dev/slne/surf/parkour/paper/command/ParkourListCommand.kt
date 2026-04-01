package dev.slne.surf.parkour.paper.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.integerArgument
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.parkour.paper.model.parkour.Parkour
import dev.slne.surf.parkour.paper.permission.ParkourPermissionRegistry
import dev.slne.surf.parkour.paper.service.ParkourService
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.CommonComponents
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.pagination.Pagination
import net.kyori.adventure.text.format.TextDecoration

fun CommandAPICommand.parkourListCommand() = subcommand("list") {
    withPermission(ParkourPermissionRegistry.COMMAND_PARKOUR_LIST)
    integerArgument("page", 1, Int.MAX_VALUE, optional = true)
    anyExecutor { executor, args ->
        val page = args.getOrDefaultUnchecked("page", 1)
        val parkours = ParkourService.parkours

        if (parkours.isEmpty()) {
            executor.sendText {
                appendErrorPrefix()
                error("Es wurde kein Parkour gefunden.")
            }
            return@anyExecutor
        }

        val pagination = Pagination<Parkour> {
            title {
                primary("Parkour Liste".toSmallCaps(), TextDecoration.BOLD)
            }

            rowRenderer { parkour, _ ->
                listOf(
                    buildText {
                        append(CommonComponents.EM_DASH)
                        appendSpace()
                        variableKey(parkour.displayName)
                        appendSpace()
                        spacer("(")
                        variableValue(parkour.players.size)
                        spacer(" Spieler)")
                        hoverEvent(buildText {
                            append(CommonComponents.EM_DASH)
                            appendSpace()
                            variableKey("Identifier:")
                            appendSpace()
                            variableValue(parkour.identifier)
                            appendNewline()
                            append(CommonComponents.EM_DASH)
                            appendSpace()
                            variableKey("Welt:")
                            appendSpace()
                            variableValue(parkour.world.name)
                        })
                    }
                )
            }
        }

        executor.sendText {
            append(pagination.renderComponent(parkours, page))
        }
    }
}