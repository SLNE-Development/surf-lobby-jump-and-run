package dev.slne.surf.parkour.command.subcommand.setting

import dev.jorel.commandapi.CommandAPICommand
import dev.slne.surf.parkour.util.Permission

class ParkourMaterialCommand(commandName: String): CommandAPICommand(commandName) {
    init {
        withPermission(Permission.COMMAND_PARKOUR_SETTING_MATERIAL)
        withSubcommand(ParkourSettingMaterialAddCommand("add"))
        withSubcommand(ParkourSettingMaterialRemoveCommand("remove"))
        withSubcommand(ParkourSettingMaterialListCommand("list"))
    }
}