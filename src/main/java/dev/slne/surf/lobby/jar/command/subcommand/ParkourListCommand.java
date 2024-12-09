package dev.slne.surf.lobby.jar.command.subcommand;

import dev.jorel.commandapi.CommandAPICommand;
import dev.slne.surf.lobby.jar.JumpAndRunProvider;
import dev.slne.surf.lobby.jar.PluginInstance;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public class ParkourListCommand extends CommandAPICommand {
  private final JumpAndRunProvider provider = PluginInstance.instance().jumpAndRunProvider();

  public ParkourListCommand(String commandName) {
    super(commandName);

    withPermission("jumpandrun.command.list");

    executesPlayer((player, args) -> {
      int playerCount = provider.jumpAndRun().getPlayers().size();

      if (playerCount == 0) {
        player.sendMessage(PluginInstance.prefix()
            .append(Component.text("Aktuell sind ", NamedTextColor.GRAY)
            .append(Component.text("keine Spieler", NamedTextColor.YELLOW))
            .append(Component.text(" im Jump And Run.", NamedTextColor.WHITE)))
        );
        return;
      }

      Component header = Component.text("Aktuell sind ", NamedTextColor.GRAY)
          .append(Component.text(playerCount + " Spieler", NamedTextColor.YELLOW))
          .append(Component.text(" im Jump And Run: ", NamedTextColor.WHITE));

      Component playerList = Component.empty();
      int current = 0;

      for (Player target : provider.jumpAndRun().getPlayers()) {
        current++;

        Component playerComponent = Component.text(target.getName(), NamedTextColor.WHITE)
            .append(Component.text(" (", NamedTextColor.GRAY))
            .append(Component.text(provider.currentPoints().get(target), NamedTextColor.YELLOW))
            .append(Component.text(")", NamedTextColor.GRAY));

        if (current < playerCount) {
          playerComponent = playerComponent.append(Component.text(", ", NamedTextColor.GRAY));
        }

        playerList = playerList.append(playerComponent);
      }

      player.sendMessage(PluginInstance.prefix().append(header.append(playerList)));
    });
  }
}
