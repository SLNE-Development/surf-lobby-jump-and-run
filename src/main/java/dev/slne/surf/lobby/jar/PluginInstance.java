package dev.slne.surf.lobby.jar;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import dev.slne.surf.lobby.jar.command.ParkourCommand;
import dev.slne.surf.lobby.jar.command.subcommand.ParkourStatsCommand;
import dev.slne.surf.lobby.jar.config.PluginConfig;
import dev.slne.surf.lobby.jar.mysql.Database;
import dev.slne.surf.lobby.jar.papi.ParkourPlaceholderExtension;
import dev.slne.surf.lobby.jar.util.PluginColor;

import lombok.Getter;
import lombok.experimental.Accessors;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
@Accessors(fluent = true)
public class PluginInstance extends JavaPlugin {
  private JumpAndRunProvider jumpAndRunProvider;
  private WorldEditPlugin worldEditPlugin;
  private Boolean worldedit;


  @Getter
  private static final Component prefix = Component.text(">> ").color(NamedTextColor.GRAY)
      .append(Component.text("Parkour").color(PluginColor.BLUE_LIGHT))
      .append(Component.text(" | ").color(NamedTextColor.DARK_GRAY));

  @Override
  public void onEnable() {
    this.jumpAndRunProvider = new JumpAndRunProvider();
    this.jumpAndRunProvider.startActionbar();

    this.handlePlaceholderAPI();
    this.handeWorldEdit();

    new ParkourCommand("parkour").register();
    new ParkourStatsCommand("stats").register();

    Bukkit.getPluginManager().registerEvents(new ParkourListener(), this);
    Database.createConnection();
  }

  @Override
  public void onDisable() {
    this.jumpAndRunProvider.stopActionbar();
    this.jumpAndRunProvider.saveAll();

    Database.closeConnection();
    PluginConfig.save(jumpAndRunProvider.jumpAndRun());
  }

  public static PluginInstance instance() {
    return getPlugin(PluginInstance.class);
  }

  private void handlePlaceholderAPI(){
    if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      new ParkourPlaceholderExtension().register();
    }
  }

  private void handeWorldEdit(){
    this.worldedit = Bukkit.getPluginManager().isPluginEnabled("WorldEdit");
  }
}
