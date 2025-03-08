package dev.slne.surf.parkour.command.subcommand

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.parkour.SurfParkour
import dev.slne.surf.parkour.database.DatabaseProvider
import dev.slne.surf.parkour.plugin

import dev.slne.surf.parkour.util.MessageBuilder
import dev.slne.surf.parkour.util.Permission
import org.bukkit.entity.Player

class ParkourToggleSoundCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(Permission.COMMAND_PARKOUR_TOGGLE)

        executesPlayer(PlayerCommandExecutor { player: Player, _: CommandArguments ->
            plugin.launch {
                val playerData = DatabaseProvider.getPlayerData(player.uniqueId)

                playerData.edit {
                    likesSound = !likesSound
                }

                if (playerData.likesSound) {
                    SurfParkour.send(player, MessageBuilder().primary("Die ParkourSounds sind nun ").success("aktiviert").primary("."))
                } else {
                    SurfParkour.send(player, MessageBuilder().primary("Die ParkourSounds sind nun ").error("deaktiviert").primary("."))
                }
            }
        })
    }
}
