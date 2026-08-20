package dev.slne.surf.parkour.paper.api

import com.google.auto.service.AutoService
import dev.slne.surf.api.paper.inventory.framework.viewFrame
import dev.slne.surf.parkour.api.SurfParkourApi
import dev.slne.surf.parkour.core.client.menu.ParkourLeaderboardPreferences
import dev.slne.surf.parkour.core.client.service.ParkourService
import dev.slne.surf.parkour.paper.menu.view.ParkourOverviewView
import net.kyori.adventure.util.Services
import org.bukkit.Bukkit
import java.util.*

@AutoService(SurfParkourApi::class)
class SurfParkourApiImpl : SurfParkourApi, Services.Fallback {
    override suspend fun showGui(playerUuid: UUID) {
        ParkourLeaderboardPreferences.setSearch(playerUuid, null)
        Bukkit.getPlayer(playerUuid)?.let {
            viewFrame.open(ParkourOverviewView::class.java, it)
        }
    }

    override fun isInParkour(playerUuid: UUID): Boolean = ParkourService.isInParkour(playerUuid)
}
