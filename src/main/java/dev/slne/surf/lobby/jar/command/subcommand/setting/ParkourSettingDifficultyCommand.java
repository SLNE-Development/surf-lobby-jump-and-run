package dev.slne.surf.lobby.jar.command.subcommand.setting;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.slne.surf.lobby.jar.PluginInstance;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;

public class ParkourSettingDifficultyCommand extends CommandAPICommand {

  public ParkourSettingDifficultyCommand(String commandName) {
    super(commandName);

    withArguments(new IntegerArgument("difficulty"));

    executesPlayer((player, args) -> {
      Integer difficulty = args.getUnchecked("difficulty");

      PluginInstance.instance().jumpAndRunProvider().jumpAndRun().setDifficulty(difficulty);

      player.sendMessage(PluginInstance.prefix().append(Component.text("Du hast die Schwierigkeit erfolgreich neu definiert.")));
    });
  }
}
