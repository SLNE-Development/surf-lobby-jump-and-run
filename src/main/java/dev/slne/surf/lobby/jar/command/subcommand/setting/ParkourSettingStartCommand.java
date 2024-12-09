package dev.slne.surf.lobby.jar.command.subcommand.setting;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.slne.surf.lobby.jar.PluginInstance;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;

public class ParkourSettingStartCommand extends CommandAPICommand {

  public ParkourSettingStartCommand(String commandName) {
    super(commandName);

    withPermission("jumpandrun.command.setting.setStart");

    withArguments(new LocationArgument("pos"));

    executesPlayer((player, args) -> {
      Location pos = args.getUnchecked("pos");

      PluginInstance.instance().jumpAndRunProvider().jumpAndRun().setStart(pos);

      player.sendMessage(PluginInstance.prefix().append(Component.text("Du hast den Start erfolgreich neu definiert.")));
    });
  }
}
