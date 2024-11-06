package dev.slne.surf.lobby.jar.command.subcommand;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.slne.surf.lobby.jar.JumpAndRunProvider;
import dev.slne.surf.lobby.jar.PluginInstance;
import dev.slne.surf.lobby.jar.util.PluginColor;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class ParkourStatsCommand extends CommandAPICommand {
  private final JumpAndRunProvider provider = PluginInstance.instance().jumpAndRunProvider();
  private final Component prefix = Component.text(">> ").color(PluginColor.DARK_GRAY);

  public ParkourStatsCommand(String commandName) {
    super(commandName);

    withArguments(new PlayerArgument("target"));

    executesPlayer((player, args) -> {
      Player target = args.getUnchecked("target");

      provider.queryHighScore(target).thenAccept(highScore -> {
        provider.queryPoints(target).thenAccept(points -> {
          player.sendMessage(prefix.append(Component.text("Statistiken von " + target.getName()).color(PluginColor.BLUE_MID)).append(Component.newline())
              .append(prefix.append(Component.text(" "))).append(Component.newline())
              .append(prefix.append(Component.text("Gesamtsprünge: ").color(PluginColor.DARK_GRAY).append(Component.text(points.toString()).color(PluginColor.GOLD)))).append(Component.newline())
              .append(prefix.append(Component.text("Aktueller Highscore: ").color(PluginColor.DARK_GRAY).append(Component.text(highScore.toString()).color(PluginColor.GOLD)))).append(Component.newline())
              .append(prefix.append(Component.text(" "))).append(Component.newline())
              .append(prefix.append(Component.text("Aktuelle Sprünge: ").color(PluginColor.DARK_GRAY).append(Component.text(provider.isJumping(target) ? provider.currentPoints().get(player).toString() : "/").color(PluginColor.GOLD))))
          );
        });
      });
    });
  }
}
