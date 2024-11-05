package dev.slne.surf.lobby.jar.command.subcommand.setting;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.slne.surf.lobby.jar.PluginInstance;
import dev.slne.surf.lobby.jar.config.PluginConfig;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;

public class ParkourSettingSaveCommand extends CommandAPICommand {

  public ParkourSettingSaveCommand(String commandName) {
    super(commandName);

    executesPlayer((player, args) -> {
      PluginConfig.save(PluginInstance.instance().jumpAndRunProvider().jumpAndRun());

      player.sendMessage(PluginInstance.prefix().append(Component.text("Du hast erfolgreich die Einstellungen gespeichert.")));
    });
  }
}
