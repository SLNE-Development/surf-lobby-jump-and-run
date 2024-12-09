package dev.slne.surf.lobby.jar.command.subcommand;

import dev.jorel.commandapi.CommandAPICommand;
import dev.slne.surf.lobby.jar.JumpAndRunProvider;
import dev.slne.surf.lobby.jar.PluginInstance;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

public class ParkourListCommand extends CommandAPICommand {
  private final JumpAndRunProvider provider = PluginInstance.instance().jumpAndRunProvider();

  public ParkourListCommand(String commandName) {
    super(commandName);

    withPermission("jumpandrun.command.list");

    executesPlayer((player, args) -> {
      StringBuilder message = new StringBuilder(provider.jumpAndRun().getPlayers().isEmpty() ? "Aktuell sind <yellow> keine Spieler<white> im Jump And Run." : "Aktuell sind <yellow>" + provider.jumpAndRun().getPlayers().size() + " Spieler<white> im Jump And Run: ");
      int current = 0;

      for(Player target : provider.jumpAndRun().getPlayers()){
        current ++;

        if(current == provider.jumpAndRun().getPlayers().size()){
          message.append("<white>").append(target.getName()).append(" <gray>(").append("<yellow>").append(provider.currentPoints().get(target)).append("<gray>)");
        }else{
          message.append("<white>").append(target.getName()).append(" <gray>(").append("<yellow>").append(provider.currentPoints().get(target)).append("<gray>), ");
        }
      }

      player.sendMessage(PluginInstance.prefix().append(MiniMessage.miniMessage().deserialize(message.toString())));
    });
  }
}
