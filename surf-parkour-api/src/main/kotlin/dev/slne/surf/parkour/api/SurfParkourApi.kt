package dev.slne.surf.parkour.api

import dev.slne.surf.surfapi.core.api.util.requiredService
import org.bukkit.entity.Player

val surfParkourApi = requiredService<SurfParkourApi>()

interface SurfParkourApi {
    suspend fun showGui(player: Player)

    fun isInParkour(player: Player): Boolean
}