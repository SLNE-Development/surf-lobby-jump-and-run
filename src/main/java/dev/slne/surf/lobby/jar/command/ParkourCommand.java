package dev.slne.surf.lobby.jar.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.slne.surf.lobby.jar.command.subcommand.ParkourListCommand;
import dev.slne.surf.lobby.jar.command.subcommand.ParkourStartCommand;
import dev.slne.surf.lobby.jar.command.subcommand.setting.ParkourSettingCommand;

public class ParkourCommand extends CommandAPICommand {

  public ParkourCommand(String commandName) {
    super(commandName);

    withPermission("surf.lobby.jar.jumpandrun.command");

    withSubcommand(new ParkourSettingCommand("setting"));
    withSubcommand(new ParkourStartCommand("start"));
    withSubcommand(new ParkourListCommand("list"));
  }
}
