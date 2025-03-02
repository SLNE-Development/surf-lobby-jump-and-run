package dev.slne.surf.parkour


import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import dev.slne.surf.parkour.command.ParkourCommand
import dev.slne.surf.parkour.command.subcommand.ParkourStatsCommand
import dev.slne.surf.parkour.config.PluginConfig
import dev.slne.surf.parkour.database.DatabaseProvider
import dev.slne.surf.parkour.listener.PlayerKickListener
import dev.slne.surf.parkour.listener.PlayerParkourListener
import dev.slne.surf.parkour.papi.ParkourPlaceholderExtension
import dev.slne.surf.parkour.service.JumpAndRunService

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

val plugin: SurfParkour get() = JavaPlugin.getPlugin(SurfParkour::class.java)
class SurfParkour : SuspendingJavaPlugin() {

    override suspend fun onEnableAsync() {
        this.handlePlaceholderAPI()

        ParkourCommand("parkour").register()
        ParkourStatsCommand("stats").register()

        Bukkit.getPluginManager().registerSuspendingEvents(PlayerParkourListener(), this)
        Bukkit.getPluginManager().registerEvents(PlayerKickListener(), this)

        DatabaseProvider.connect()
        DatabaseProvider.fetchParkours()

        JumpAndRunService.startActionbar()
    }

    override suspend fun onDisableAsync() {
        JumpAndRunService.stopActionbar()
        JumpAndRunService.saveAll()

        DatabaseProvider.saveParkours()

        PluginConfig.save(JumpAndRunService.jumpAndRun)
    }

    private fun handlePlaceholderAPI() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            ParkourPlaceholderExtension().register()
        }
    }
}
