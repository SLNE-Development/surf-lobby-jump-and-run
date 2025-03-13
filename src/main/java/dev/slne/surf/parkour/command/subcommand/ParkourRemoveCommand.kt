package dev.slne.surf.parkour.command.subcommand

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.stringArgument
import dev.slne.surf.parkour.command.argument.parkourArgument
import dev.slne.surf.parkour.database.DatabaseProvider
import dev.slne.surf.parkour.parkour.Parkour
import dev.slne.surf.parkour.send
import dev.slne.surf.parkour.util.Permission
import dev.slne.surf.surfapi.core.api.messages.adventure.clickSuggestsCommand

class ParkourRemoveCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(Permission.COMMAND_PARKOUR_REMOVE)

        parkourArgument("parkour")
        stringArgument("confirm", true)

        anyExecutor { sender, args ->
            val parkour: Parkour by args
            val confirm = args.getOrDefaultUnchecked("confirm", "")

            if (confirm.isNotEmpty() && confirm == "--confirm") {
                DatabaseProvider.getParkours().remove(parkour)

                sender.send {
                    success("Der Parkour ")
                    variableValue(parkour.name)
                    success(" wurde erfolgreich gelöscht.")
                }

                return@anyExecutor
            }

            sender.send {
                info("Bist du dir sicher, dass du den Parkour ")
                variableValue(parkour.name)
                info(" löschen möchtest?")
                appendNewPrefixedLine()
                error("Alle Daten des Parkours werden gelöscht und sind nicht wiederherstellbar!")
                // hoverEvent(HoverEvent.showText(error("Klicke hier, um den Parkour endgültig zu löschen"))) // TODO: fix
                clickSuggestsCommand("/parkour delete ${parkour.name} --confirm")
            }
        }
    }
}