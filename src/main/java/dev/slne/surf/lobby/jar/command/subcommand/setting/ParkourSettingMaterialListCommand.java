package dev.slne.surf.lobby.jar.command.subcommand.setting;

import dev.jorel.commandapi.CommandAPICommand;

import dev.slne.surf.lobby.jar.JumpAndRunProvider;
import dev.slne.surf.lobby.jar.PluginInstance;

import dev.slne.surf.lobby.jar.util.PluginColor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class ParkourSettingMaterialListCommand extends CommandAPICommand {
  private final JumpAndRunProvider provider = PluginInstance.instance().jumpAndRunProvider();

  public ParkourSettingMaterialListCommand(String commandName) {
    super(commandName);

    withPermission("jumpandrun.command.setting.listmaterial");
    executesPlayer((player, args) -> {
      int materialCount = provider.jumpAndRun().getMaterials().size();

      Component header = Component.text("Materialien im Jump And Run: ", PluginColor.LIGHT_GRAY)
          .append(Component.text("(", PluginColor.DARK_GRAY)
              .append(Component.text(materialCount, NamedTextColor.YELLOW))
              .append(Component.text(") ", PluginColor.DARK_GRAY))
          );

      Component materialList = this.getComponent(materialCount);

      player.sendMessage(PluginInstance.prefix().append(header.append(materialList)));
    });
  }

  private @NotNull Component getComponent(int materialCount) {
    Component materialList = Component.text("");
    int current = 0;

    for (Material material : provider.jumpAndRun().getMaterials()) {
      current++;
      Component materialComponent = Component.text(material.name(), NamedTextColor.WHITE);

      if (current < materialCount) {
        materialList = materialList.append(materialComponent).append(Component.text(", ", NamedTextColor.GRAY));
      } else {
        materialList = materialList.append(materialComponent);
      }
    }
    return materialList;
  }
}
