package dev.slne.surf.parkour


import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import dev.slne.surf.parkour.config.PluginConfig
import dev.slne.surf.parkour.listener.PlayerKickListener
import dev.slne.surf.parkour.listener.PlayerParkourListener
import dev.slne.surf.parkour.papi.ParkourPlaceholderExtension
import dev.slne.surf.parkour.service.JumpAndRunService

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

val plugin: PluginInstance get() = JavaPlugin.getPlugin(PluginInstance::class.java)
class PluginInstance : SuspendingJavaPlugin() {

    override suspend fun onEnableAsync() {
        this.handlePlaceholderAPI()

        dev.slne.surf.parkour.command.ParkourCommand("parkour").register()
        dev.slne.surf.parkour.command.subcommand.ParkourStatsCommand("stats").register()

        Bukkit.getPluginManager().registerSuspendingEvents(PlayerParkourListener(), this)
        Bukkit.getPluginManager().registerEvents(PlayerKickListener(), this)
        dev.slne.surf.parkour.database.DatabaseProvider.connect()

        JumpAndRunService.startActionbar()
    }

    override suspend fun onDisableAsync() {
        JumpAndRunService.stopActionbar()
        JumpAndRunService.saveAll()

        PluginConfig.save(JumpAndRunService.jumpAndRun)
    }

    private fun handlePlaceholderAPI() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            ParkourPlaceholderExtension().register()
        }
    }
}
