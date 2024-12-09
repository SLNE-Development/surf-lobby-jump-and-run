package dev.slne.surf.lobby.jar.command.subcommand.setting;

import dev.jorel.commandapi.CommandAPICommand;

import dev.slne.surf.lobby.jar.JumpAndRunProvider;
import dev.slne.surf.lobby.jar.PluginInstance;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;

public class ParkourSettingMaterialListCommand extends CommandAPICommand {
  private final JumpAndRunProvider provider = PluginInstance.instance().jumpAndRunProvider();

  public ParkourSettingMaterialListCommand(String commandName) {
    super(commandName);
    withPermission("jumpandrun.command.setting.listmaterial");

    executesPlayer((player, args) -> {
      StringBuilder message = new StringBuilder("Materialien im Jump And Run: <gray>(<yellow>" + provider.jumpAndRun().getMaterials().size() + "<gray>) <white>");
      int current = 0;

      for(Material material : provider.jumpAndRun().getMaterials()){
        current ++;

        if(current == provider.jumpAndRun().getMaterials().size()){
          message.append("<white>").append(material.name());
        }else{
          message.append("<white>").append(material.name()).append("<gray>, ");
        }
      }

      player.sendMessage(PluginInstance.prefix().append(MiniMessage.miniMessage().deserialize(message.toString())));
    });
  }
}
