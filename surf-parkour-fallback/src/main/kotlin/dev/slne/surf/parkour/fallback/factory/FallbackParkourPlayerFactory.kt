package dev.slne.surf.parkour.fallback.factory

import com.google.auto.service.AutoService
import dev.slne.surf.parkour.api.entity.ParkourPlayer
import dev.slne.surf.parkour.core.entity.CoreParkourPlayer
import dev.slne.surf.parkour.core.factory.ParkourPlayerFactory
import dev.slne.surf.parkour.core.service.parkourPlayerService
import dev.slne.surf.parkour.core.util.loadProfileTexture
import dev.slne.surf.surfapi.core.api.service.PlayerLookupService
import net.kyori.adventure.util.Services
import java.util.*

@AutoService(ParkourPlayerFactory::class)
class FallbackParkourPlayerFactory : ParkourPlayerFactory, Services.Fallback {
    override suspend fun createPlayer(uuid: UUID): ParkourPlayer {
        val name =
            PlayerLookupService.getUsername(uuid) ?: error("Player with UUID $uuid not found")
        val texture = loadProfileTexture(uuid)
        val player = CoreParkourPlayer(uuid, name, texture)

        parkourPlayerService.insertPlayer(player)
        return player
    }

    override suspend fun createPlayer(name: String): ParkourPlayer {
        val uuid = PlayerLookupService.getUuid(name) ?: error("Player with name $name not found")
        val texture = loadProfileTexture(uuid)
        val player = CoreParkourPlayer(uuid, name, texture)

        parkourPlayerService.insertPlayer(player)
        return player
    }
}