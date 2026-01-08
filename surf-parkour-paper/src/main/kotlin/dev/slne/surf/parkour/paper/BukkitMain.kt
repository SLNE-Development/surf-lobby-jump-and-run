package dev.slne.surf.parkour.paper

import com.github.retrooper.packetevents.PacketEvents
import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.database.DatabaseManager
import dev.slne.surf.parkour.paper.command.parkourCommand
import dev.slne.surf.parkour.paper.config.ParkourConfiguration
import dev.slne.surf.parkour.paper.database.ParkourRunsTable
import dev.slne.surf.parkour.paper.database.ParkourTable
import dev.slne.surf.parkour.paper.hook.PolarHook
import dev.slne.surf.parkour.paper.hook.VulcanHook
import dev.slne.surf.parkour.paper.listener.FailureListener
import dev.slne.surf.parkour.paper.listener.ParkourItemListener
import dev.slne.surf.parkour.paper.listener.PlayerPacketListener
import dev.slne.surf.parkour.paper.listener.SuccessListener
import dev.slne.surf.parkour.paper.service.ParkourService
import dev.slne.surf.parkour.paper.service.parkourService
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


