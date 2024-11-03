package dev.slne.surf.lobby.jar.command.subcommand.debug;

import dev.jorel.commandapi.CommandAPICommand;

public class ParkourDebugCommand extends CommandAPICommand {

  public ParkourDebugCommand(String commandName) {
    super(commandName);

    withPermission("surf.lobby.jar.jumpandrun.command.debug");

    withSubcommand(new ParkourDebugGenerateCommand("generate"));
  }
}
