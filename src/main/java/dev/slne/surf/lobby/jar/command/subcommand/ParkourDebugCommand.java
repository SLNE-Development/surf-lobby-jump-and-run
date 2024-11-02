package dev.slne.surf.lobby.jar.command.subcommand;

import dev.jorel.commandapi.CommandAPICommand;
import dev.slne.surf.lobby.jar.command.subcommand.debug.ParkourDebugGenerateCommand;

public class ParkourDebugCommand extends CommandAPICommand {

  public ParkourDebugCommand(String commandName) {
    super(commandName);

    withPermission("surf.lobby.jar.jumpandrun.command.debug");

    withSubcommand(new ParkourDebugGenerateCommand("generate"));
  }
}
