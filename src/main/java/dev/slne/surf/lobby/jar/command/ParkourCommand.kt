package dev.slne.surf.lobby.jar.command

import dev.jorel.commandapi.CommandAPICommand
import dev.slne.surf.lobby.jar.command.subcommand.ParkourListCommand
import dev.slne.surf.lobby.jar.command.subcommand.ParkourStartCommand
import dev.slne.surf.lobby.jar.command.subcommand.ParkourStatsCommand
import dev.slne.surf.lobby.jar.command.subcommand.ParkourToggleSoundCommand
import dev.slne.surf.lobby.jar.command.subcommand.setting.ParkourSettingCommand

class ParkourCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("jumpandrun.command")

        withSubcommand(ParkourSettingCommand("setting"))
        withSubcommand(ParkourStartCommand("start"))
        withSubcommand(ParkourListCommand("list"))
        withSubcommand(ParkourStatsCommand("stats"))
        withSubcommand(ParkourToggleSoundCommand("toggleSound"))
    }
}
