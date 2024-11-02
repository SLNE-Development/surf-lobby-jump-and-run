package dev.slne.surf.lobby.jar.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.slne.surf.lobby.jar.command.subcommand.ParkourDebugCommand;

public class ParkourCommand extends CommandAPICommand {

  public ParkourCommand(String commandName) {
    super(commandName);

    withPermission("surf.lobby.jar.jumpandrun.command");

    withSubcommand(new ParkourDebugCommand("debug"));
  }
}
