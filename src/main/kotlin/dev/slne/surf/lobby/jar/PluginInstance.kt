package dev.slne.surf.lobby.jar

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import com.sk89q.worldedit.bukkit.WorldEditPlugin
import dev.slne.surf.lobby.jar.command.ParkourCommand
import dev.slne.surf.lobby.jar.command.subcommand.ParkourStatsCommand
import dev.slne.surf.lobby.jar.config.PluginConfig
import dev.slne.surf.lobby.jar.listener.ParkourListener
import dev.slne.surf.lobby.jar.listener.PlayerKickListener
import dev.slne.surf.lobby.jar.papi.ParkourPlaceholderExtension
import dev.slne.surf.lobby.jar.service.JumpAndRunService
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

val plugin: PluginInstance get() = JavaPlugin.getPlugin(PluginInstance::class.java)

class PluginInstance : SuspendingJavaPlugin() {

    var worldEditInstance: WorldEditPlugin? = null
    var worldedit = false

    override suspend fun onEnableAsync() {
        JumpAndRunService.startTask()

        this.handlePlaceholderAPI()
        this.handeWorldEdit()

        ParkourCommand("parkour").register()
        ParkourStatsCommand("stats").register()

        Bukkit.getPluginManager().registerSuspendingEvents(ParkourListener, this)
        Bukkit.getPluginManager().registerSuspendingEvents(PlayerKickListener, this)
    }

    override suspend fun onDisableAsync() {
        JumpAndRunService.stopTask()
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
}
