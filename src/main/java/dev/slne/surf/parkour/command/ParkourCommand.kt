package dev.slne.surf.parkour.command

import dev.jorel.commandapi.CommandAPICommand
import dev.slne.surf.parkour.command.subcommand.ParkourListCommand
import dev.slne.surf.parkour.command.subcommand.ParkourStartCommand
import dev.slne.surf.parkour.command.subcommand.ParkourStatsCommand
import dev.slne.surf.parkour.command.subcommand.ParkourToggleSoundCommand
import dev.slne.surf.parkour.command.subcommand.setting.ParkourMaterialCommand
import dev.slne.surf.parkour.command.subcommand.setting.ParkourSettingCommand

class ParkourCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("jumpandrun.command")

        withSubcommand(ParkourSettingCommand("setting"))
        withSubcommand(ParkourStartCommand("start"))
        withSubcommand(ParkourListCommand("list"))
        withSubcommand(ParkourStatsCommand("stats"))
        withSubcommand(ParkourToggleSoundCommand("toggleSound"))
        withSubcommand(ParkourMaterialCommand("material"))
    }
}
