package dev.slne.surf.lobby.jar.command.subcommand.setting;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;

import dev.slne.surf.lobby.jar.PluginInstance;

import java.util.Arrays;
import java.util.stream.Collectors;

import net.kyori.adventure.text.Component;

import org.bukkit.Material;

public class ParkourSettingMaterialRemoveCommand extends CommandAPICommand {

  public ParkourSettingMaterialRemoveCommand(String commandName) {
    super(commandName);
    withPermission("jumpandrun.command.setting.removematerial");

    withArguments(new StringArgument("material").replaceSuggestions(ArgumentSuggestions.strings(Arrays.stream(Material.values()).map(Material::name).collect(Collectors.toList()))));

    executesPlayer((player, args) -> {
      Material material = Material.getMaterial(args.getUnchecked("material"));

      if(material == null){
        throw CommandAPI.failWithString("Das Material wurde nicht gefunden.");
      }

      PluginInstance.instance().jumpAndRunProvider().jumpAndRun().getMaterials().remove(material);

      player.sendMessage(PluginInstance.prefix().append(Component.text(String.format("Du hast %s aus der Liste der Materialien entfernt.", material.name()))));
    });
  }
}
