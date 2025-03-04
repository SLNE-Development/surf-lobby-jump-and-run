package dev.slne.surf.parkour.command.argument

import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.CustomArgument.CustomArgumentInfoParser
import dev.jorel.commandapi.arguments.StringArgument

import dev.slne.surf.parkour.database.DatabaseProvider
import dev.slne.surf.parkour.parkour.Parkour

class ParkourArgument(nodeName: String): CustomArgument<Parkour, String> (
    StringArgument(nodeName),
    CustomArgumentInfoParser { info: CustomArgumentInfo<String> ->
        return@CustomArgumentInfoParser Parkour.getByName(info.input()) ?: throw CustomArgumentException.fromMessageBuilder(MessageBuilder("Unknown parkour: ").appendArgInput())
    }) {
    init {
        this.replaceSuggestions(ArgumentSuggestions.strings {
            DatabaseProvider.getParkours().map { it.name }.toTypedArray()
            }
        )
    }
}