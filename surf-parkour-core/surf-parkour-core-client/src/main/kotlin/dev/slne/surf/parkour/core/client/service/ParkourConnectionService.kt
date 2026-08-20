package dev.slne.surf.parkour.core.client.service

import dev.slne.surf.parkour.core.client.platform.ParkourPlatform
import java.util.*

/**
 * Keeps a player's parkour state in step with them joining and leaving the server.
 */
object ParkourConnectionService {

    /**
     * Caches the stats of the player identified by [playerUuid], so menus can show them without
     * waiting for a round trip.
     */
    fun onJoin(playerUuid: UUID) {
        ParkourPlatform.launch {
            ParkourRunsService.loadAndCacheStats(playerUuid)
        }
    }

    /**
     * Ends the run of the player identified by [playerUuid], counting it as a fall.
     */
    fun onQuit(playerUuid: UUID) {
        val parkour = ParkourService.getParkourByPlayer(playerUuid) ?: return
        val generator = parkour.getGenerator(playerUuid) ?: return

        if (generator.isRunning()) {
            ParkourService.triggerFailure(playerUuid)
        }
    }
}
