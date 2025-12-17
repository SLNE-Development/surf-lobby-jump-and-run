package dev.slne.surf.parkour

import com.github.retrooper.packetevents.PacketEvents
import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.database.DatabaseManager
import dev.slne.surf.parkour.command.parkourCommand
import dev.slne.surf.parkour.config.ParkourConfiguration
import dev.slne.surf.parkour.database.ParkourRunsTable
import dev.slne.surf.parkour.database.ParkourTable
import dev.slne.surf.parkour.hook.PolarHook
import dev.slne.surf.parkour.hook.VulcanHook
import dev.slne.surf.parkour.listener.FailureListener
import dev.slne.surf.parkour.listener.ParkourItemListener
import dev.slne.surf.parkour.listener.PlayerPacketListener
import dev.slne.surf.parkour.listener.SuccessListener
import dev.slne.surf.parkour.service.ParkourService
import dev.slne.surf.parkour.service.parkourService
import dev.slne.surf.surfapi.bukkit.api.event.register
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

val plugin get() = JavaPlugin.getPlugin(BukkitMain::class.java)

class BukkitMain : SuspendingJavaPlugin() {
    override fun onEnable() {
        FailureListener().register()
        SuccessListener().register()
        ParkourItemListener().register()

        PolarHook().register()
        VulcanHook().register()

        PacketEvents.getAPI().eventManager.registerListener(PlayerPacketListener())

        parkourCommand()

        establishDatabaseConnection()
        parkourService.loadParkours()
        ParkourService.startUpdating()
    }

    override fun onDisable() {
        ParkourService.stopUpdating()
    }

    private fun establishDatabaseConnection() {
        DatabaseManager(plugin.dataPath, plugin.dataPath).databaseProvider.connect()

        transaction {
            SchemaUtils.create(ParkourTable, ParkourRunsTable)
        }
    }

    val parkourConfig = ParkourConfiguration()
}

val config get() = plugin.parkourConfig.config


