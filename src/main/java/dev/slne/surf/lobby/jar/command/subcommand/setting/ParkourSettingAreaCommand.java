package dev.slne.surf.lobby.jar.command.subcommand.setting;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.slne.surf.lobby.jar.PluginInstance;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;

public class ParkourSettingAreaCommand extends CommandAPICommand {

  public ParkourSettingAreaCommand(String commandName) {
    super(commandName);

    withArguments(new LocationArgument("pos1"));
    withArguments(new LocationArgument("pos2"));

    executesPlayer((player, args) -> {
      Location pos1 = args.getUnchecked("pos1");
      Location pos2 = args.getUnchecked("pos2");

      PluginInstance.instance().jumpAndRunProvider().jumpAndRun().setPosOne(pos1);
      PluginInstance.instance().jumpAndRunProvider().jumpAndRun().setPosTwo(pos2);

      player.sendMessage(PluginInstance.prefix().append(Component.text("Du hast die Arena erfolgreich neu definiert.")));
    });
  }
}
