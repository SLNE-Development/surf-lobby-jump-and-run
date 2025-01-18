package dev.slne.surf.lobby.jar

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.SuspendingPlugin
import com.sk89q.worldedit.bukkit.WorldEditPlugin
import dev.slne.surf.lobby.jar.command.ParkourCommand
import dev.slne.surf.lobby.jar.command.subcommand.ParkourStatsCommand
import dev.slne.surf.lobby.jar.config.PluginConfig
import dev.slne.surf.lobby.jar.listener.ParkourListener
import dev.slne.surf.lobby.jar.listener.PlayerKickListener
import dev.slne.surf.lobby.jar.mysql.Database
import dev.slne.surf.lobby.jar.papi.ParkourPlaceholderExtension
import dev.slne.surf.lobby.jar.service.JumpAndRunService
import dev.slne.surf.lobby.jar.util.PluginColor
import lombok.Getter
import lombok.experimental.Accessors
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

@Getter
@Accessors(fluent = true)
class PluginInstance : SuspendingJavaPlugin() {
    var worldEditInstance: WorldEditPlugin? = null
    var worldedit = false


    override suspend fun onEnableAsync() {
        JumpAndRunService.startActionbar()

        this.handlePlaceholderAPI()
        this.handeWorldEdit()

        ParkourCommand("parkour").register()
        ParkourStatsCommand("stats").register()

        Bukkit.getPluginManager().registerEvents(ParkourListener(), this)
        Bukkit.getPluginManager().registerEvents(PlayerKickListener(), this)
        Database.createConnection()
    }

    override suspend fun onDisableAsync() {
        JumpAndRunService.stopActionbar()
      JumpAndRunService.saveAll().join()

        Database.closeConnection()
        PluginConfig.save(JumpAndRunService.jumpAndRun)
    }

    private fun handlePlaceholderAPI() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            ParkourPlaceholderExtension().register()
        }
    }

    private fun handeWorldEdit() {
        this.worldedit = Bukkit.getPluginManager().isPluginEnabled("WorldEdit")
        this.worldEditInstance =
            Bukkit.getPluginManager().getPlugin("WorldEdit") as WorldEditPlugin?
    }

    companion object {
        @Getter
        val prefix: Component = Component.text(">> ", NamedTextColor.GRAY)
            .append(Component.text("Parkour", PluginColor.BLUE_LIGHT))
            .append(Component.text(" | ", NamedTextColor.DARK_GRAY))

        @JvmStatic
        fun instance(): PluginInstance {
            return getPlugin(PluginInstance::class.java)
        }
    }
}
