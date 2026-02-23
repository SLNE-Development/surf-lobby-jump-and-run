package dev.slne.surf.parkour.paper.api

import com.google.auto.service.AutoService
import dev.slne.surf.parkour.api.SurfParkourApi
import dev.slne.surf.parkour.paper.menu.view.ParkourOverviewView
import dev.slne.surf.parkour.paper.service.parkourService
import dev.slne.surf.surfapi.bukkit.api.inventory.framework.viewFrame
import net.kyori.adventure.util.Services
import org.bukkit.entity.Player

@AutoService(SurfParkourApi::class)
class SurfParkourApiImpl : SurfParkourApi, Services.Fallback {
    override suspend fun showGui(player: Player) {
        viewFrame.open(ParkourOverviewView::class.java, player)
    }

    override fun isInParkour(player: Player): Boolean = parkourService.isInParkour(player.uniqueId)
}