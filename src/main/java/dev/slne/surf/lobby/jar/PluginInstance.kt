package dev.slne.surf.lobby.jar

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.folia.registerSuspendingEvents

import dev.slne.surf.lobby.jar.command.ParkourCommand
import dev.slne.surf.lobby.jar.command.subcommand.ParkourStatsCommand
import dev.slne.surf.lobby.jar.config.PluginConfig
import dev.slne.surf.lobby.jar.listener.PlayerParkourListener
import dev.slne.surf.lobby.jar.listener.PlayerKickListener
import dev.slne.surf.lobby.jar.mysql.Database
import dev.slne.surf.lobby.jar.papi.ParkourPlaceholderExtension
import dev.slne.surf.lobby.jar.service.JumpAndRunService
import dev.slne.surf.lobby.jar.util.DispatcherUtil
import dev.slne.surf.lobby.jar.util.PluginColor

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
val plugin: PluginInstance get() = JavaPlugin.getPlugin(PluginInstance::class.java)
class PluginInstance : SuspendingJavaPlugin() {

    override suspend fun onEnableAsync() {
        JumpAndRunService.startActionbar()

        this.handlePlaceholderAPI()

        ParkourCommand("parkour").register()
        ParkourStatsCommand("stats").register()

        Bukkit.getPluginManager().registerSuspendingEvents(PlayerParkourListener(), this, DispatcherUtil.PARKOUR)
        Bukkit.getPluginManager().registerEvents(PlayerKickListener(), this)
        Database.createConnection()
    }

    override suspend fun onDisableAsync() {
        JumpAndRunService.stopActionbar()
        JumpAndRunService.saveAll()

        Database.closeConnection()
        PluginConfig.save(JumpAndRunService.jumpAndRun)
    }

    private fun handlePlaceholderAPI() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            ParkourPlaceholderExtension().register()
        }
    }


    companion object {
        val prefix: Component = Component.text(">> ", NamedTextColor.GRAY)
            .append(Component.text("Parkour", PluginColor.BLUE_LIGHT))
            .append(Component.text(" | ", NamedTextColor.DARK_GRAY))

        @JvmStatic
        fun instance(): PluginInstance {
            return getPlugin(PluginInstance::class.java)
        }
    }
}
