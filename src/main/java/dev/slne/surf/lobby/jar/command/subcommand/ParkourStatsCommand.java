package dev.slne.surf.lobby.jar.command.subcommand;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.slne.surf.lobby.jar.JumpAndRunProvider;
import dev.slne.surf.lobby.jar.PluginInstance;
import dev.slne.surf.lobby.jar.util.PluginColor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

public class ParkourStatsCommand extends CommandAPICommand {
  private final JumpAndRunProvider provider = PluginInstance.instance().jumpAndRunProvider();

  public ParkourStatsCommand(String commandName) {
    super(commandName);

    withArguments(new PlayerArgument("target"));

    executesPlayer((player, args) -> {
      Player target = args.getUnchecked("target");

      provider.queryHighScore(target).thenAccept(highScore -> {
        provider.queryPoints(target).thenAccept(points -> {
          player.sendMessage(createStatisticMessage(points.toString(), highScore.toString(), provider.isJumping(target) ? provider.currentPoints().get(player).toString() : "Kein laufender Parkour", target.getName()));
        });
      });
    });
  }

  public static Component createStatisticMessage(String points, String highScore, String current, String player) {
    return Component.text(">> ", PluginColor.DARK_GRAY)
        .append(Component.text("Parkour ", PluginColor.BLUE))
        .append(Component.text("| ",PluginColor.DARK_GRAY))
        .append(Component.text("-------------", PluginColor.LIGHT_GRAY))
        .append(Component.text("STATISTIK", PluginColor.BLUE_LIGHT).decorate(TextDecoration.BOLD))
        .append(Component.text("-------------", PluginColor.LIGHT_GRAY))
        .append(Component.newline())
        .append(Component.text(">> ",PluginColor.DARK_GRAY))
        .append(Component.text("Parkour ", PluginColor.BLUE))
        .append(Component.text("|",PluginColor.DARK_GRAY))
        .append(Component.newline())
        .append(Component.text(">> ",PluginColor.DARK_GRAY))
        .append(Component.text("Parkour ", PluginColor.BLUE))
        .append(Component.text("| ",PluginColor.DARK_GRAY))
        .append(Component.text("Seit Aufzeichnung:", PluginColor.DARK_GRAY))
        .append(Component.newline())
        .append(Component.text(">> ",PluginColor.DARK_GRAY))
        .append(Component.text("Parkour ", PluginColor.BLUE))
        .append(Component.text("|",PluginColor.DARK_GRAY))
        .append(Component.newline())
        .append(Component.text(">> ",PluginColor.DARK_GRAY))
        .append(Component.text("Parkour ", PluginColor.BLUE))
        .append(Component.text("|    ",PluginColor.DARK_GRAY))
        .append(Component.text("Sprünge: ", PluginColor.BLUE_MID))
        .append(Component.text(points, PluginColor.GOLD))
        .append(Component.newline())
        .append(Component.text(">> ",PluginColor.DARK_GRAY))
        .append(Component.text("Parkour ", PluginColor.BLUE))
        .append(Component.text("|    ",PluginColor.DARK_GRAY))
        .append(Component.text("Rekord: ", PluginColor.BLUE_MID))
        .append(Component.text(highScore, PluginColor.GOLD))
        .append(Component.newline())
        .append(Component.text(">> ",PluginColor.DARK_GRAY))
        .append(Component.text("Parkour ", PluginColor.BLUE))
        .append(Component.text("|",PluginColor.DARK_GRAY))
        .append(Component.newline())
        .append(Component.text(">> ",PluginColor.DARK_GRAY))
        .append(Component.text("Parkour ", PluginColor.BLUE))
        .append(Component.text("| Laufender Parkour:",PluginColor.DARK_GRAY))
        .append(Component.newline())
        .append(Component.text(">> ",PluginColor.DARK_GRAY))
        .append(Component.text("Parkour ", PluginColor.BLUE))
        .append(Component.text("|",PluginColor.DARK_GRAY))
        .append(Component.newline())
        .append(Component.text(">> ",PluginColor.DARK_GRAY))
        .append(Component.text("Parkour ", PluginColor.BLUE))
        .append(Component.text("|    ",PluginColor.DARK_GRAY))
        .append(Component.text("Sprünge: ", PluginColor.BLUE_MID))
        .append(Component.text(current, PluginColor.GOLD))
        .append(Component.newline())
        .append(Component.text(">> ",PluginColor.DARK_GRAY))
        .append(Component.text("Parkour ", PluginColor.BLUE))
        .append(Component.text("|",PluginColor.DARK_GRAY))
        .append(Component.newline())
        .append(Component.text(">> ",PluginColor.DARK_GRAY))
        .append(Component.text("Parkour ", PluginColor.BLUE))
        .append(Component.text("| ",PluginColor.DARK_GRAY))
        .append(Component.text("-----------------------------------", PluginColor.LIGHT_GRAY));
  }
}
