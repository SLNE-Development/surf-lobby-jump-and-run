package dev.slne.surf.parkour.paper

import com.github.retrooper.packetevents.PacketEvents
import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.database.DatabaseApi
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.SchemaUtils
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.parkour.paper.command.parkourCommand
import dev.slne.surf.parkour.paper.config.ParkourConfiguration
import dev.slne.surf.parkour.paper.database.table.ParkourPlayerTexturesTable
import dev.slne.surf.parkour.paper.database.table.ParkourRunsTable
import dev.slne.surf.parkour.paper.database.table.ParkourTable
import dev.slne.surf.parkour.paper.hook.PolarHook
import dev.slne.surf.parkour.paper.hook.VulcanHook
import dev.slne.surf.parkour.paper.listener.FailureListener
import dev.slne.surf.parkour.paper.listener.PlayerPacketListener
import dev.slne.surf.parkour.paper.listener.SuccessListener
import dev.slne.surf.parkour.paper.menu.view.ParkourActivePlayersView
import dev.slne.surf.parkour.paper.menu.view.ParkourLeaderboardView
import dev.slne.surf.parkour.paper.menu.view.ParkourOverviewView
import dev.slne.surf.parkour.paper.service.ParkourService
import dev.slne.surf.parkour.paper.service.parkourService
import dev.slne.surf.surfapi.bukkit.api.event.register
import dev.slne.surf.surfapi.bukkit.api.inventory.framework.viewFrame
import kotlinx.coroutines.runBlocking
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(BukkitMain::class.java)

class BukkitMain : SuspendingJavaPlugin() {

    override suspend fun onLoadAsync() {
        viewFrame.with(ParkourLeaderboardView)
        viewFrame.with(ParkourOverviewView)
        viewFrame.with(ParkourActivePlayersView)
    }

    override suspend fun onEnableAsync() {
        FailureListener.register()
        SuccessListener.register()

        PolarHook().register()
        VulcanHook().register()

        PacketEvents.getAPI().eventManager.registerListener(PlayerPacketListener())

        parkourCommand()

        establishDatabaseConnection()
        parkourService.loadParkours()
        parkourService.loadStats()
        ParkourService.startUpdating()
    }

    override fun onDisable() {
        ParkourService.stopUpdating()
    }

    private fun establishDatabaseConnection() {
        DatabaseApi.create(plugin.dataPath)

        runBlocking {
            suspendTransaction {
                SchemaUtils.create(ParkourTable, ParkourRunsTable, ParkourPlayerTexturesTable)
            }
        }
    }

    val parkourConfig = ParkourConfiguration()
}

val config get() = plugin.parkourConfig.config


