package dev.slne.surf.parkour.paper.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.parkour.paper.hook.WorldEditHook
import dev.slne.surf.parkour.paper.permission.ParkourPermissionRegistry
import dev.slne.surf.parkour.paper.service.parkourService
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.Location
import java.util.*

fun CommandAPICommand.parkourCreateCommand() = subcommand("create") {
    withPermission(ParkourPermissionRegistry.COMMAND_PARKOUR_CREATE)
    stringArgument("identifier")
    textArgument("displayname")
    locationArgument("respawnStanding")
    playerExecutor { player, args ->
        val identifier: String by args
        val displayname: String by args
        val respawnStanding: Location by args

        if (identifier.contains(" ")) {
            player.sendText {
                appendPrefix()
                error("Die Kennung darf keine Leerzeichen enthalten.")
            }
            return@playerExecutor
        }

        if (parkourService.exists(identifier)) {
            player.sendText {
                appendPrefix()
                error("Ein Parkour mit dieser Kennung existiert bereits.")
            }
            return@playerExecutor
        }

        if (!WorldEditHook.isEnabled()) {
            player.sendText {
                appendPrefix()
                error("Es kann keine WorldEdit-Auswahl gefunden werden, da das WorldEdit-Plugin nicht installiert ist.")
            }
            return@playerExecutor
        }

        val selection = WorldEditHook.getSelection(player)

        if (selection == null) {
            player.sendText {
                appendPrefix()
                error("Es wurde keine WorldEdit-Auswahl gefunden. Bitte wähle einen Bereich aus und versuche es erneut.")
            }
            return@playerExecutor
        }

        parkourService.createParkour(
            UUID.randomUUID(),
            identifier,
            displayname,
            selection,
            player.world,
            respawnStanding
        )

        player.sendText {
            appendPrefix()
            success("Der Parkour ")
            variableValue(displayname)
            success(" wurde erstellt.")
        }
    }
}