package dev.slne.surf.parkour.paper.service

import com.github.benmanes.caffeine.cache.Caffeine
import dev.slne.surf.parkour.api.data.PlayerTextures
import dev.slne.surf.parkour.paper.database.repository.playerTextureRepository
import dev.slne.surf.parkour.paper.plugin
import java.util.*
import kotlin.system.measureTimeMillis

val playerTextureService = PlayerTextureService()

class PlayerTextureService {
    private val textureCache = Caffeine.newBuilder().build<UUID, PlayerTextures>()
    val textures get() = textureCache.asMap().values

    suspend fun saveTexture(texture: PlayerTextures) {
        textureCache.put(texture.playerUuid, texture)
        playerTextureRepository.saveTexture(texture)
    }

    fun getTexture(playerUuid: UUID) =
        textureCache.getIfPresent(playerUuid) ?: PlayerTextures.empty()

    suspend fun loadTextures() {
        plugin.logger.info("Loading player textures, this may also take a while...")

        val ms = measureTimeMillis {
            val textures = playerTextureRepository.fetchTextures()
            textures.forEach { textureCache.put(it.playerUuid, it) }
        }

        plugin.logger.info("Loaded textures for ${textureCache.asMap().size} players in ${ms}ms!")
    }
}