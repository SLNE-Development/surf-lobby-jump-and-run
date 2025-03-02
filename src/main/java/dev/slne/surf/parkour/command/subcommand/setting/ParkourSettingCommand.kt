package dev.slne.surf.parkour.command.subcommand.setting

import dev.jorel.commandapi.CommandAPICommand

class ParkourSettingCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("jumpandrun.command.setting")

        withSubcommand(ParkourSettingAreaCommand("setArea"))
        withSubcommand(ParkourSettingSpawnCommand("setSpawn"))
        withSubcommand(ParkourSettingStartCommand("setStart"))
        withSubcommand(ParkourSettingMaterialListCommand("listMaterials"))
        withSubcommand(ParkourSettingMaterialRemoveCommand("removeMaterial"))
        withSubcommand(ParkourSettingMaterialAddCommand("addMaterial"))
        withSubcommand(ParkourSettingSaveCommand("saveConfiguration"))
    }
}
