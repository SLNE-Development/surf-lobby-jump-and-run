package dev.slne.surf.parkour.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.parkour.command.subcommand.*
import dev.slne.surf.parkour.command.subcommand.setting.material.ParkourMaterialCommand
import dev.slne.surf.parkour.command.subcommand.setting.ParkourSettingCommand
import dev.slne.surf.parkour.menu.ParkourMenu
import dev.slne.surf.parkour.util.Permission

class ParkourCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(Permission.COMMAND_PARKOUR)

        subcommand(ParkourSettingCommand("setting"))
        subcommand(ParkourStartCommand("start"))
        subcommand(ParkourListCommand("list"))
        subcommand(ParkourStatsCommand("stats"))
        subcommand(ParkourToggleSoundCommand("toggleSound"))
        subcommand(ParkourMaterialCommand("material"))
        subcommand(ParkourCreateCommand("create"))
        subcommand(ParkourRemoveCommand("delete"))
        subcommand(ParkourBetaModeCommand("betaMode"))

        playerExecutor { player, _ -> ParkourMenu.lazyOpen(player) }
    }
}

