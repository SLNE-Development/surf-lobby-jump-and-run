package dev.slne.surf.parkour.minestom.command.subcommand

import dev.slne.minestom.lobby.api.command.commandapi.dsl.blockPositionArgument
import dev.slne.minestom.lobby.api.command.commandapi.dsl.locationArgument
import dev.slne.minestom.lobby.api.command.commandapi.dsl.playerExecutor
import dev.slne.minestom.lobby.api.command.commandapi.dsl.stringArgument
import dev.slne.minestom.lobby.api.command.commandapi.dsl.subcommand
import dev.slne.minestom.lobby.api.command.commandapi.dsl.textArgument
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.parkour.core.client.command.appendIdentifierContainsSpaces
import dev.slne.surf.parkour.core.client.command.appendIdentifierTaken
import dev.slne.surf.parkour.core.client.command.appendParkourCreated
import dev.slne.surf.parkour.core.client.model.geometry.ParkourLocation
import dev.slne.surf.parkour.core.client.model.geometry.ParkourRegion
import dev.slne.surf.parkour.core.client.model.geometry.ParkourVector
import dev.slne.surf.parkour.core.client.permission.ParkourPermissions
import dev.slne.surf.parkour.core.client.service.ParkourService
import dev.slne.surf.parkour.minestom.instance.parkourWorldName
import net.minestom.server.coordinate.Vec
import java.util.*

/**
 * Creates a parkour from two corners of the area it runs in.
 *
 * Unlike the Paper command the area is not taken from a world edit selection, because no such
 * plugin exists here; both corners are named as block positions instead.
 */
fun parkourCreateCommand() = subcommand("create") {
    withPermission(ParkourPermissions.COMMAND_PARKOUR_CREATE)
    stringArgument("identifier")
    textArgument("displayname")
    locationArgument("respawnStanding")
    blockPositionArgument("corner1")
    blockPositionArgument("corner2")
    playerExecutor { player, args ->
        val identifier = args.get<String>("identifier")
        val displayname = args.get<String>("displayname")
        val respawnStanding = args.get<Vec>("respawnStanding")
        val corner1 = args.get<Vec>("corner1")
        val corner2 = args.get<Vec>("corner2")

        if (identifier.contains(" ")) {
            player.sendText { appendIdentifierContainsSpaces() }
            return@playerExecutor
        }

        if (ParkourService.exists(identifier)) {
            player.sendText { appendIdentifierTaken() }
            return@playerExecutor
        }

        val world = player.instance?.parkourWorldName ?: run {
            player.sendText {
                appendErrorPrefix()
                error("Die Welt, in der du dich befindest, hat keinen Namen.")
            }
            return@playerExecutor
        }

        ParkourService.createParkour(
            UUID.randomUUID(),
            identifier,
            displayname,
            ParkourRegion.of(corner1.toParkourVector(), corner2.toParkourVector()),
            world,
            ParkourLocation(world, respawnStanding.x(), respawnStanding.y(), respawnStanding.z())
        )

        player.sendText { appendParkourCreated(displayname) }
    }
}

private fun Vec.toParkourVector() = ParkourVector(x(), y(), z())
