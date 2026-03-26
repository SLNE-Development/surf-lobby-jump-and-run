package dev.slne.surf.parkour.core.paper.service

import com.github.benmanes.caffeine.cache.Caffeine
import com.google.auto.service.AutoService
import dev.slne.surf.parkour.api.data.PlayerTextures
import dev.slne.surf.parkour.core.common.rabbit.packet.request.LoadAllPlayerTexturesRequestPacket
import dev.slne.surf.parkour.core.common.rabbit.packet.request.SavePlayerTexturesRequestPacket
import dev.slne.surf.parkour.core.common.service.ParkourTexturesService
import dev.slne.surf.parkour.core.paper.PaperParkourInstance
import net.kyori.adventure.util.Services
import java.util.*
import kotlin.system.measureTimeMillis

@AutoService(ParkourTexturesService::class)
class ParkourTexturesServiceImpl : ParkourTexturesService, Services.Fallback {
    private val textureCache = Caffeine.newBuilder().build<UUID, PlayerTextures>()
    override val textures: List<PlayerTextures> get() = textureCache.asMap().values.toList()

    override suspend fun saveTexture(texture: PlayerTextures) {
        textureCache.put(texture.playerUuid, texture)
        PaperParkourInstance.rabbitApi.sendRequest(SavePlayerTexturesRequestPacket(texture))
    }

    override fun getTexture(playerUuid: UUID) =
        textureCache.getIfPresent(playerUuid) ?: PlayerTextures.empty()

    override suspend fun loadTextures() = measureTimeMillis {
        val textures =
            PaperParkourInstance.rabbitApi.sendRequest(LoadAllPlayerTexturesRequestPacket).playerTextures
        textures.forEach { textureCache.put(it.playerUuid, it) }
    }
}