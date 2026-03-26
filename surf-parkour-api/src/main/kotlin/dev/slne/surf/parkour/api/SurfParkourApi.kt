package dev.slne.surf.parkour.api

import dev.slne.surf.surfapi.core.api.util.requiredService
import java.util.*

val surfParkourApi = requiredService<SurfParkourApi>()

interface SurfParkourApi {
    suspend fun showGui(playerUuid: UUID)

    fun isInParkour(playerUuid: UUID): Boolean
}