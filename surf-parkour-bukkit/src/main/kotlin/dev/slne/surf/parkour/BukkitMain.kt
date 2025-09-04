package dev.slne.surf.parkour

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.parkour.config.ParkourConfiguration
import dev.slne.surf.parkour.core.service.databaseService
import dev.slne.surf.parkour.core.service.parkourService
import dev.slne.surf.surfapi.bukkit.api.metrics.Metrics
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(BukkitMain::class.java)

lateinit var metrics: Metrics

class BukkitMain : SuspendingJavaPlugin() {
    override fun onLoad() {
        databaseService.connect(plugin.dataPath)
        databaseService.createTables()
    }

    override suspend fun onEnableAsync() {
        BukkitCommandManager.registerCommands()
        BukkitListenerManager.registerBukkitListeners()
        BukkitListenerManager.registerExternalListeners()

        metrics = Metrics(this, 27168)

        parkourService.fetchParkours(parkourConfig.config.serverUuid)
    }

    override fun onDisable() {
        if (::metrics.isInitialized) {
            metrics.shutdown()
        }

        databaseService.disconnect()
    }

    val parkourConfig = ParkourConfiguration()
}
