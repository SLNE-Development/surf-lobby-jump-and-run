package dev.slne.surf.parkour.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.parkour.command.subcommand.*
import dev.slne.surf.parkour.command.subcommand.setting.ParkourMaterialCommand
import dev.slne.surf.parkour.command.subcommand.setting.ParkourSettingCommand
import dev.slne.surf.parkour.gui.ParkourMenu
import dev.slne.surf.parkour.util.Permission
import org.bukkit.entity.Player

class ParkourCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(Permission.COMMAND_PARKOUR)

        withSubcommand(ParkourSettingCommand("setting"))
        withSubcommand(ParkourStartCommand("start"))
        withSubcommand(ParkourListCommand("list"))
        withSubcommand(ParkourStatsCommand("stats"))
        withSubcommand(ParkourToggleSoundCommand("toggleSound"))
        withSubcommand(ParkourMaterialCommand("material"))
        withSubcommand(ParkourCreateCommand("create"))
        withSubcommand(ParkourRemoveCommand("remove"))

        executesPlayer(PlayerCommandExecutor() { player: Player, _: CommandArguments ->
            ParkourMenu(player)
        })
    }
}

