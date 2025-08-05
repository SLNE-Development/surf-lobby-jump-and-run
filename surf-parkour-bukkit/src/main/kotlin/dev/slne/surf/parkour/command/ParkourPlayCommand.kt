package dev.slne.surf.parkour.command

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.parkour.api.model.parkour.Parkour
import dev.slne.surf.parkour.command.argument.parkourArgument
import dev.slne.surf.parkour.plugin
import dev.slne.surf.parkour.util.ParkourPermissionRegistry
import dev.slne.surf.parkour.util.parkourPlayer
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun CommandAPICommand.parkourPlayCommand() = subcommand("play") {
    withPermission(ParkourPermissionRegistry.COMMAND_PARKOUR_PLAY)
    parkourArgument("parkour")
    playerExecutor { player, args ->
        val parkour: Parkour by args

        plugin.launch {
            parkour.start(player.parkourPlayer())

            player.sendText {
                appendPrefix()
                success("Du hast den Parkour ")
                variableValue(parkour.name)
                success(" gestartet.")
            }
        }
    }
}