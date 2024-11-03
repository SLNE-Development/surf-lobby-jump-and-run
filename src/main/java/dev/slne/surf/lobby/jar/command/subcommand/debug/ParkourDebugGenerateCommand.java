package dev.slne.surf.lobby.jar.command.subcommand.debug;

import dev.jorel.commandapi.CommandAPICommand;
import dev.slne.surf.lobby.jar.PluginInstance;
import net.kyori.adventure.text.Component;

public class ParkourDebugGenerateCommand extends CommandAPICommand {

  public ParkourDebugGenerateCommand(String commandName) {
    super(commandName);

    executesPlayer((player, args) -> {
      PluginInstance.instance().jumpAndRunProvider().start(player);

      player.sendMessage(PluginInstance.prefix().append(Component.text("Es wurde ein neuer Block generiert.")));
    });
  }
}
