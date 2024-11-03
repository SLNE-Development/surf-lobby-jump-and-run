package dev.slne.surf.lobby.jar.command.subcommand.setting;

import dev.jorel.commandapi.CommandAPICommand;

public class ParkourSettingCommand extends CommandAPICommand {

  public ParkourSettingCommand(String commandName) {
    super(commandName);

    withPermission("surf.lobby.jar.jumpandrun.command.setting");

    withSubcommand(new ParkourSettingAreaCommand("area"));
    withSubcommand(new ParkourSettingDifficultyCommand("difficulty"));
  }
}
