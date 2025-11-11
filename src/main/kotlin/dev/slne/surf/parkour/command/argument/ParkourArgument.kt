package dev.slne.surf.parkour.command.argument

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.parkour.model.parkour.Parkour
import dev.slne.surf.parkour.service.parkourService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

class ParkourArgument(nodeName: String) : CustomArgument<Parkour, String>(
    StringArgument(nodeName),
    CustomArgumentInfoParser { info ->
        parkourService.getParkour(info.input)
            ?: throw CustomArgumentException.fromAdventureComponent {
                buildText {
                    appendPrefix()
                    error("Der Parkour ")
                    variableValue(info.input)
                    error("wurde nicht gefunden.")
                }
            }
    }) {
    init {
        replaceSuggestions(ArgumentSuggestions.stringCollection {
            parkourService.getParkours().map { it.identifier }
        })
    }
}

inline fun CommandAPICommand.parkourArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandAPICommand = withArguments(ParkourArgument(nodeName).setOptional(optional).apply(block))