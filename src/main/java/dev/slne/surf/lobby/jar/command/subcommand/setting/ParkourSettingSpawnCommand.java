package dev.slne.surf.lobby.jar.command.subcommand.setting;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.slne.surf.lobby.jar.PluginInstance;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;

public class ParkourSettingSpawnCommand extends CommandAPICommand {

  public ParkourSettingSpawnCommand(String commandName) {
    super(commandName);

    withArguments(new LocationArgument("pos"));

    executesPlayer((player, args) -> {
      Location pos = args.getUnchecked("pos");

      PluginInstance.instance().jumpAndRunProvider().jumpAndRun().setSpawn(pos);

      player.sendMessage(PluginInstance.prefix().append(Component.text("Du hast den Spawn erfolgreich neu definiert.")));
    });
  }
}
