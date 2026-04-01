package dev.slne.surf.parkour.api

import dev.slne.surf.surfapi.core.api.util.requiredService
import java.util.*

private val surfParkourApi = requiredService<SurfParkourApi>()

interface SurfParkourApi {
    suspend fun showGui(playerUuid: UUID)

    fun isInParkour(playerUuid: UUID): Boolean

    companion object : SurfParkourApi by surfParkourApi {
        val INSTANCE get() = surfParkourApi
    }
}