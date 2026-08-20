package dev.slne.surf.parkour.minestom.api

import com.google.auto.service.AutoService
import dev.slne.minestom.lobby.api.extension.ConnectionManager
import dev.slne.surf.api.minestom.inventory.framework.open
import dev.slne.surf.parkour.api.SurfParkourApi
import dev.slne.surf.parkour.core.client.menu.ParkourLeaderboardPreferences
import dev.slne.surf.parkour.core.client.service.ParkourService
import dev.slne.surf.parkour.minestom.menu.view.ParkourOverviewView
import net.kyori.adventure.util.Services
import java.util.*

@AutoService(SurfParkourApi::class)
class SurfParkourApiImpl : SurfParkourApi, Services.Fallback {
    override suspend fun showGui(playerUuid: UUID) {
        ParkourLeaderboardPreferences.setSearch(playerUuid, null)
        ConnectionManager.getOnlinePlayerByUuid(playerUuid)?.let {
            ParkourOverviewView.open(it)
        }
    }

    override fun isInParkour(playerUuid: UUID): Boolean = ParkourService.isInParkour(playerUuid)
}
