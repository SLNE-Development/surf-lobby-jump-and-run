package dev.slne.surf.parkour


import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents

import dev.slne.surf.parkour.command.ParkourCommand
import dev.slne.surf.parkour.command.subcommand.ParkourStatsCommand
import dev.slne.surf.parkour.database.DatabaseProvider
import dev.slne.surf.parkour.listener.PlayerKickListener
import dev.slne.surf.parkour.listener.PlayerParkourListener
//import dev.slne.surf.parkour.papi.ParkourPlaceholderExtension
import dev.slne.surf.parkour.util.Colors
import dev.slne.surf.parkour.util.MessageBuilder
import kotlinx.coroutines.Dispatchers

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

val instance: SurfParkour get() = JavaPlugin.getPlugin(SurfParkour::class.java)
class SurfParkour : SuspendingJavaPlugin() {

    override suspend fun onEnableAsync() {
        this.handlePlaceholderAPI()

        ParkourCommand("parkour").register()
        ParkourStatsCommand("stats").register()

        Bukkit.getPluginManager().registerSuspendingEvents(PlayerParkourListener(), this)
        Bukkit.getPluginManager().registerEvents(PlayerKickListener(), this)

        DatabaseProvider.connect()
        DatabaseProvider.fetchParkours()
    }

    override suspend fun onDisableAsync() {
        DatabaseProvider.saveParkours()
        DatabaseProvider.saveAllPlayers()
    }

    private fun handlePlaceholderAPI() {
//        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
//            ParkourPlaceholderExtension().register()
//        }
    }

    companion object {
        fun send(player: Player, message: MessageBuilder) {
            player.sendMessage(Colors.PREFIX.append(message.build()))
        }
    }
}
