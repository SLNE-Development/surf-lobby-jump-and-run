package dev.slne.surf.parkour.core.paper.service

import com.github.benmanes.caffeine.cache.Caffeine
import com.google.auto.service.AutoService
import dev.slne.surf.api.core.util.toObjectList
import dev.slne.surf.parkour.api.data.PlayerTextures
import dev.slne.surf.parkour.core.common.rabbit.packet.request.LoadAllPlayerTexturesRequestPacket
import dev.slne.surf.parkour.core.common.rabbit.packet.request.SavePlayerTexturesRequestPacket
import dev.slne.surf.parkour.core.paper.PaperParkourInstance
import net.kyori.adventure.util.Services
import java.util.*
import kotlin.system.measureTimeMillis

@AutoService(ParkourTexturesService::class)
class ParkourTexturesServiceImpl : ParkourTexturesService, Services.Fallback {
    private val textureCache = Caffeine.newBuilder()
        .build<UUID, PlayerTextures>()

    override val textures = textureCache.asMap().values.toObjectList()

    override suspend fun saveTexture(texture: PlayerTextures) {
        textureCache.put(texture.playerUuid, texture)
        PaperParkourInstance.rabbitApi.sendRequest(SavePlayerTexturesRequestPacket(texture))
    }

    override fun getTexture(playerUuid: UUID) =
        textureCache.getIfPresent(playerUuid) ?: PlayerTextures.empty()

    override suspend fun loadTextures() = measureTimeMillis {
        val textures = PaperParkourInstance.rabbitApi
            .sendRequest(LoadAllPlayerTexturesRequestPacket()).playerTextures.map {
                it.copy(
                    texture = "ewogICJ0aW1lc3RhbXAiIDogMTc3NTA2MjI5NTk2NiwKICAicHJvZmlsZUlkIiA6ICI4NmRhMzAzYjBmOTA0M2JhYWU3ZmJkMjNjZGJmYjBiYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJKaW56YXJ1IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2Y4NWVkMzg2ZDZmOWFmYmI1MjJkMWQ1ZGJmNDI3YTA5YWFhZTM5YjUxYmI5MWY0MWI2NjQ4NDZjMWRlYjExYzUiCiAgICB9LAogICAgIkNBUEUiIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzI4ZGU0YTgxNjg4YWQxOGI0OWU3MzVhMjczZTA4NmMxOGYxZTM5NjY5NTYxMjNjY2I1NzQwMzRjMDZmNWQzMzYiCiAgICB9CiAgfQp9"
                )
            }.associateBy { it.playerUuid }

        textureCache.putAll(textures)
    }
}