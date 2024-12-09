package dev.slne.surf.lobby.jar.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.slne.surf.lobby.jar.command.subcommand.ParkourListCommand;
import dev.slne.surf.lobby.jar.command.subcommand.ParkourStartCommand;
import dev.slne.surf.lobby.jar.command.subcommand.ParkourStatsCommand;
import dev.slne.surf.lobby.jar.command.subcommand.ParkourToggleSoundCommand;
import dev.slne.surf.lobby.jar.command.subcommand.setting.ParkourSettingCommand;

public class ParkourCommand extends CommandAPICommand {

  public ParkourCommand(String commandName) {
    super(commandName);

    withPermission("jumpandrun.command");

    withSubcommand(new ParkourSettingCommand("setting"));
    withSubcommand(new ParkourStartCommand("start"));
    withSubcommand(new ParkourListCommand("list"));
    withSubcommand(new ParkourStatsCommand("stats"));
    withSubcommand(new ParkourToggleSoundCommand("toggleSound"));
  }
}
