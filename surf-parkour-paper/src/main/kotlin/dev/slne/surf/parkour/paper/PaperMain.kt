package dev.slne.surf.parkour.paper

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.parkour.core.common.service.parkourRunsService
import dev.slne.surf.parkour.core.common.service.playerTextureService
import dev.slne.surf.parkour.core.paper.PaperParkourInstance
import dev.slne.surf.parkour.paper.command.parkourCommand
import dev.slne.surf.parkour.paper.config.ParkourConfiguration
import dev.slne.surf.parkour.paper.hook.PolarHook
import dev.slne.surf.parkour.paper.hook.VulcanHook
import dev.slne.surf.parkour.paper.listener.FailureListener
import dev.slne.surf.parkour.paper.listener.JoinListener
import dev.slne.surf.parkour.paper.listener.SuccessListener
import dev.slne.surf.parkour.paper.menu.view.ParkourActivePlayersView
import dev.slne.surf.parkour.paper.menu.view.ParkourLeaderboardView
import dev.slne.surf.parkour.paper.menu.view.ParkourOverviewView
import dev.slne.surf.parkour.paper.service.ParkourService
import dev.slne.surf.parkour.paper.service.parkourService
import dev.slne.surf.surfapi.bukkit.api.event.register
import dev.slne.surf.surfapi.bukkit.api.inventory.framework.viewFrame
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)

class PaperMain : SuspendingJavaPlugin() {
    lateinit var parkourConfig: ParkourConfiguration


    override suspend fun onLoadAsync() {
        PaperParkourInstance.paperLoader.onLoad()

        viewFrame.with(ParkourLeaderboardView)
        viewFrame.with(ParkourOverviewView)
        viewFrame.with(ParkourActivePlayersView)
    }

    override suspend fun onEnableAsync() {
        parkourConfig = ParkourConfiguration()

        PaperParkourInstance.paperLoader.onEnable()

        FailureListener.register()
        SuccessListener.register()
        JoinListener.register()

        PolarHook().register()
        VulcanHook().register()

        parkourCommand()

        parkourService.loadParkours()
        parkourRunsService.loadStats()
        playerTextureService.loadTextures()
        ParkourService.startUpdating()
    }

    override suspend fun onDisableAsync() {
        ParkourService.stopUpdating()
        PaperParkourInstance.paperLoader.onDisable()
    }
}

val config get() = plugin.parkourConfig.config


