package dev.slne.surf.parkour.paper.command.argument

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.parkour.paper.model.parkour.Parkour
import dev.slne.surf.parkour.paper.service.ParkourService

class ParkourArgument(nodeName: String) : CustomArgument<Parkour, String>(
    StringArgument(nodeName),
    CustomArgumentInfoParser { info ->
        ParkourService.getParkour(info.input)
            ?: throw CustomArgumentException.fromAdventureComponent {
                buildText {
                    appendErrorPrefix()
                    error("Der Parkour ")
                    variableValue(info.input)
                    error(" wurde nicht gefunden.")
                }
            }
    }) {
    init {
        replaceSuggestions(ArgumentSuggestions.stringCollection {
            ParkourService.parkours.map { it.identifier }
        })
    }
}

inline fun CommandAPICommand.parkourArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandAPICommand = withArguments(ParkourArgument(nodeName).setOptional(optional).apply(block))