package dev.slne.surf.parkour.command.subcommand.setting

import dev.jorel.commandapi.CommandAPICommand
import dev.slne.surf.parkour.util.Permission

class ParkourSettingCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(Permission.COMMAND_PARKOUR_SETTING)

        withSubcommand(ParkourSettingAreaCommand("setArea"))
        withSubcommand(ParkourSettingSpawnCommand("setSpawn"))
        withSubcommand(ParkourSettingStartCommand("setStart"))
    }
}
