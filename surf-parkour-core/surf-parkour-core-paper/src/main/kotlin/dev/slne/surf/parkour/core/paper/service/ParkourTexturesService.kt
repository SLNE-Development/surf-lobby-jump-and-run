package dev.slne.surf.parkour.core.paper.service

import dev.slne.surf.parkour.api.data.PlayerTextures
import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectList
import org.jetbrains.annotations.UnmodifiableView
import java.util.*

private val service = requiredService<ParkourTexturesService>()

interface ParkourTexturesService {
    val textures: @UnmodifiableView ObjectList<PlayerTextures>

    fun getTexture(playerUuid: UUID): PlayerTextures

    suspend fun saveTexture(texture: PlayerTextures)
    suspend fun loadTextures(): Long

    companion object : ParkourTexturesService by service {
        val INSTANCE get() = service
    }
}