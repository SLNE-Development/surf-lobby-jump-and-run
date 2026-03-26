package dev.slne.surf.parkour.core.common.service

import dev.slne.surf.parkour.api.data.PlayerTextures
import dev.slne.surf.surfapi.core.api.util.requiredService
import java.util.*

val playerTextureService = requiredService<ParkourTexturesService>()

interface ParkourTexturesService {
    val textures: List<PlayerTextures>
    suspend fun saveTexture(texture: PlayerTextures)
    fun getTexture(playerUuid: UUID): PlayerTextures
    suspend fun loadTextures(): Long
}