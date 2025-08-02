package dev.slne.surf.parkour

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.parkour.command.parkourCommand
import dev.slne.surf.parkour.config.ParkourConfiguration
import org.bukkit.plugin.java.JavaPlugin

class BukkitMain : SuspendingJavaPlugin() {
    override fun onEnable() {
        parkourCommand()
    }

    override fun onDisable() {
        super.onDisable()
    }
}

val plugin = JavaPlugin.getPlugin(BukkitMain::class.java)

val parkourConfig = ParkourConfiguration()