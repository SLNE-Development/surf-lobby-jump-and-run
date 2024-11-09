package dev.slne.surf.lobby.jar.command.subcommand;

import dev.jorel.commandapi.CommandAPICommand;

import dev.slne.surf.lobby.jar.PluginInstance;

public class ParkourStartCommand extends CommandAPICommand {

  public ParkourStartCommand(String commandName) {
    super(commandName);

    executesPlayer((player, args) -> {
      PluginInstance.instance().jumpAndRunProvider().start(player);
    });
  }
}
