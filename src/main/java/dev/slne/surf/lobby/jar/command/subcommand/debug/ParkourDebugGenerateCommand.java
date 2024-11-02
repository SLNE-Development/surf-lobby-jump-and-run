package dev.slne.surf.lobby.jar.command.subcommand.debug;

import dev.jorel.commandapi.CommandAPICommand;
import dev.slne.surf.lobby.jar.PluginInstance;

public class ParkourDebugGenerateCommand extends CommandAPICommand {

  public ParkourDebugGenerateCommand(String commandName) {
    super(commandName);

    executesPlayer((player, args) -> {
      PluginInstance.instance().jumpAndRunProvider().generateBlock(player);
    });
  }
}
