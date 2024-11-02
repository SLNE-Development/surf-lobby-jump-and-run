package dev.slne.surf.lobby.jar;

import dev.slne.surf.lobby.jar.command.ParkourCommand;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
@Accessors(fluent = true)
public class PluginInstance extends JavaPlugin {
  private JumpAndRunProvider jumpAndRunProvider;

  @Override
  public void onEnable() {
    this.jumpAndRunProvider = new JumpAndRunProvider();

    new ParkourCommand("parkour").register();
  }

  public static PluginInstance instance(){
    return getPlugin(PluginInstance.class);
  }
}
