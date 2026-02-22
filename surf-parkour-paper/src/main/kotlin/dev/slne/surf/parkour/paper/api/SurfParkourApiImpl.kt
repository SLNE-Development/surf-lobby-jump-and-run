package dev.slne.surf.parkour.paper.api

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import com.google.auto.service.AutoService
import dev.slne.surf.parkour.api.SurfParkourApi
import dev.slne.surf.parkour.paper.menu.ParkourMenu
import dev.slne.surf.parkour.paper.model.parkour.PersonalParkourSummary
import dev.slne.surf.parkour.paper.plugin
import dev.slne.surf.parkour.paper.service.parkourService
import kotlinx.coroutines.withContext
import net.kyori.adventure.util.Services
import org.bukkit.entity.Player

@AutoService(SurfParkourApi::class)
class SurfParkourApiImpl : SurfParkourApi, Services.Fallback {
    override suspend fun showGui(player: Player) {
//        val stats = parkourService.getRuns(player.uniqueId)
        val summary = PersonalParkourSummary(player.uniqueId, stats, player.name)

        withContext(plugin.entityDispatcher(player)) {
            ParkourMenu(summary).open(player)
        }
    }

    override fun isInParkour(player: Player): Boolean = parkourService.isInParkour(player.uniqueId)
}