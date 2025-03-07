package dev.slne.surf.parkour


import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents

import dev.slne.surf.parkour.command.ParkourCommand
import dev.slne.surf.parkour.command.subcommand.ParkourStatsCommand
import dev.slne.surf.parkour.database.DatabaseProvider
import dev.slne.surf.parkour.listener.PlayerKickListener
import dev.slne.surf.parkour.listener.PlayerParkourListener
import dev.slne.surf.parkour.listener.PlayerQuitListener

import dev.slne.surf.parkour.util.Colors
import dev.slne.surf.parkour.util.MessageBuilder
import fr.skytasul.glowingentities.GlowingBlocks

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(SurfParkour::class.java)
class SurfParkour : SuspendingJavaPlugin() {

    lateinit var blockApi: GlowingBlocks

    override suspend fun onEnableAsync() {
        this.saveDefaultConfig()
        this.blockApi = GlowingBlocks(this)

        ParkourCommand("parkour").register()
        ParkourStatsCommand("stats").register()

        Bukkit.getPluginManager().registerSuspendingEvents(PlayerParkourListener(), this)
        Bukkit.getPluginManager().registerEvents(PlayerKickListener(), this)
        Bukkit.getPluginManager().registerEvents(PlayerQuitListener(), this)

        DatabaseProvider.connect()
        DatabaseProvider.fetchParkours()

    }

    override suspend fun onDisableAsync() {
        DatabaseProvider.saveParkours()
        DatabaseProvider.savePlayers()
    }

    companion object {
        fun send(player: Player, message: MessageBuilder) {
            player.sendMessage(Colors.PREFIX.append(message.build()))
        }
    }
}
