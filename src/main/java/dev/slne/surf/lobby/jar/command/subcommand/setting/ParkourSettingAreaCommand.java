package dev.slne.surf.lobby.jar.command.subcommand.setting;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.internal.annotation.Selection;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import dev.jorel.commandapi.CommandAPICommand;
import dev.slne.surf.lobby.jar.JumpAndRun;
import dev.slne.surf.lobby.jar.PluginInstance;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ParkourSettingAreaCommand extends CommandAPICommand {

  public ParkourSettingAreaCommand(String commandName) {
    super(commandName);

    withPermission("jumpandrun.command.setting.area");

    executesPlayer((player, args) -> {

      if(!PluginInstance.instance().worldedit()) {
        player.sendMessage(PluginInstance.prefix().append(Component.text("Bitte installiere WorldEdit um diesen Command auszuführen.")));
        return;
      }

      WorldEditPlugin worldEditPlugin = PluginInstance.instance().worldEditPlugin();

      if (worldEditPlugin == null) {
        player.sendMessage(PluginInstance.prefix().append(Component.text("WorldEdit ist nicht verfügbar!")));
        return;
      }

      LocalSession session = worldEditPlugin.getSession(player);
      try {
        Region region = session.getSelection(session.getSelectionWorld());


        BlockVector3 pos1 = region.getMinimumPoint();
        BlockVector3 pos2 = region.getMaximumPoint();

        JumpAndRun jumpAndRun = PluginInstance.instance().jumpAndRunProvider().jumpAndRun();
        jumpAndRun.setPosOne(new Location(player.getWorld(), pos1.getX(), pos1.getY(), pos1.getZ()));
        jumpAndRun.setPosTwo(new Location(player.getWorld(), pos2.getX(), pos2.getY(), pos2.getZ()));

        player.sendMessage(PluginInstance.prefix().append(Component.text("Du hast die Arena erfolgreich neu definiert.")));

      } catch (IncompleteRegionException e) {
        player.sendMessage(PluginInstance.prefix().append(Component.text("Bitte wähle einen Bereich mit WorldEdit aus!")));
      }
    });
  }
}
