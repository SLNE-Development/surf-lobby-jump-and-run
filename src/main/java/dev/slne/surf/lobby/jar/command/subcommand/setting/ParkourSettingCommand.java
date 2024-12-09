package dev.slne.surf.lobby.jar.command.subcommand.setting;

import dev.jorel.commandapi.CommandAPICommand;

public class ParkourSettingCommand extends CommandAPICommand {

  public ParkourSettingCommand(String commandName) {
    super(commandName);

    withPermission("jumpandrun.command.setting");

    withSubcommand(new ParkourSettingAreaCommand("setArea"));
    withSubcommand(new ParkourSettingSpawnCommand("setSpawn"));
    withSubcommand(new ParkourSettingStartCommand("setStart"));
    withSubcommand(new ParkourSettingMaterialListCommand("listMaterials"));
    withSubcommand(new ParkourSettingMaterialRemoveCommand("removeMaterial"));
    withSubcommand(new ParkourSettingMaterialAddCommand("addMaterial"));
    withSubcommand(new ParkourSettingSaveCommand("saveConfiguration"));
  }
}
