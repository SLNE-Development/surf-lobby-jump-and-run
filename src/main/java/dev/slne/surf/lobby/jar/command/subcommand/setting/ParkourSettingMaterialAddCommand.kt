package dev.slne.surf.lobby.jar.command.subcommand.setting;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;

import dev.slne.surf.lobby.jar.PluginInstance;
import dev.slne.surf.lobby.jar.command.argument.MaterialArgument;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;

public class ParkourSettingMaterialAddCommand extends CommandAPICommand {
  public ParkourSettingMaterialAddCommand(String commandName) {
    super(commandName);

    withPermission("jumpandrun.command.setting.addmaterial");
    withArguments(MaterialArgument.argument("material"));

    executesPlayer((player, args) -> {
      Material material = args.getUnchecked("material");

      if(material == null){
        throw CommandAPI.failWithString("Das Material wurde nicht gefunden.");
      }

      PluginInstance.instance().jumpAndRunProvider().jumpAndRun().getMaterials().add(material);

      player.sendMessage(PluginInstance.prefix().append(Component.text(String.format("Du hast %s zur Liste der Materialien hinzugefügt.", material.name()))));
    });
  }
}
