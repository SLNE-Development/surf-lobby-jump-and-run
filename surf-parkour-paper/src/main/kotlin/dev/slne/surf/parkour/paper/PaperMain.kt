package dev.slne.surf.parkour.paper

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.api.paper.event.register
import dev.slne.surf.api.paper.inventory.framework.viewFrame
import dev.slne.surf.parkour.core.client.ClientParkourInstance
import dev.slne.surf.parkour.core.client.service.ParkourRunsService
import dev.slne.surf.parkour.core.client.service.ParkourService
import dev.slne.surf.parkour.core.client.service.ParkourTexturesService
import dev.slne.surf.parkour.paper.command.parkourCommand
import dev.slne.surf.parkour.paper.listener.ParkourListener
import dev.slne.surf.parkour.paper.menu.view.ParkourActivePlayersView
import dev.slne.surf.parkour.paper.menu.view.ParkourLeaderboardView
import dev.slne.surf.parkour.paper.menu.view.ParkourOverviewView
import dev.slne.surf.parkour.paper.service.ParkourActionBarTask
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)

class PaperMain : SuspendingJavaPlugin() {
    override suspend fun onLoadAsync() {
        ClientParkourInstance.clientLoader.onLoad()

        viewFrame.with(ParkourLeaderboardView)
        viewFrame.with(ParkourOverviewView)
        viewFrame.with(ParkourActivePlayersView)
    }

    override suspend fun onEnableAsync() {
        ClientParkourInstance.clientLoader.onEnable()

        ParkourListener.register()

        parkourCommand()

        ParkourService.loadParkours()
        ParkourRunsService.loadStats()
        ParkourTexturesService.loadTextures()
        ParkourActionBarTask.startUpdating()
    }

    override suspend fun onDisableAsync() {
        ParkourActionBarTask.stopUpdating()
        ClientParkourInstance.clientLoader.onDisable()
    }
}
