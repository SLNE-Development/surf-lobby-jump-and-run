package dev.slne.surf.parkour.paper.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.parkour.core.client.command.appendIdentifierContainsSpaces
import dev.slne.surf.parkour.core.client.command.appendIdentifierTaken
import dev.slne.surf.parkour.core.client.command.appendParkourCreated
import dev.slne.surf.parkour.core.client.service.ParkourService
import dev.slne.surf.parkour.paper.hook.WorldEditHook
import dev.slne.surf.parkour.paper.permission.ParkourPermissionRegistry
import dev.slne.surf.parkour.paper.util.toParkourLocation
import dev.slne.surf.parkour.paper.util.toParkourRegion
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
            player.sendText { appendIdentifierContainsSpaces() }
            return@playerExecutor
        }

        if (ParkourService.exists(identifier)) {
            player.sendText { appendIdentifierTaken() }
            return@playerExecutor
        }

        if (!WorldEditHook.isEnabled()) {
            player.sendText {
                appendErrorPrefix()
                error("Es kann keine WorldEdit-Auswahl gefunden werden, da das WorldEdit-Plugin nicht installiert ist.")
            }
            return@playerExecutor
        }

        val selection = WorldEditHook.getSelection(player)

        if (selection == null) {
            player.sendText {
                appendErrorPrefix()
                error("Es wurde keine WorldEdit-Auswahl gefunden. Bitte wähle einen Bereich aus und versuche es erneut.")
            }
            return@playerExecutor
        }

        ParkourService.createParkour(
            UUID.randomUUID(),
            identifier,
            displayname,
            selection.toParkourRegion(),
            player.world.name,
            respawnStanding.toParkourLocation()
        )

        player.sendText { appendParkourCreated(displayname) }
    }
}
