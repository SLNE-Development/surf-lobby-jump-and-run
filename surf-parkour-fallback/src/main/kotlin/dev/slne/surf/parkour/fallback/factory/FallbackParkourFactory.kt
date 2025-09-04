package dev.slne.surf.parkour.fallback.factory

import com.google.auto.service.AutoService
import dev.slne.surf.parkour.api.model.parkour.ParkourCreationData
import dev.slne.surf.parkour.core.factory.ParkourFactory
import dev.slne.surf.parkour.fallback.model.FallbackParkour
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import net.kyori.adventure.util.Services
import java.util.*

@AutoService(ParkourFactory::class)
class FallbackParkourFactory : ParkourFactory, Services.Fallback {
    override fun createParkour(parkourData: ParkourCreationData) = FallbackParkour(
        UUID.randomUUID(),
        parkourData.name,
        mutableObjectSetOf(),
        mutableObject2ObjectMapOf(),
        mutableObject2ObjectMapOf(),
        parkourData.spawn,
        parkourData.corner1,
        parkourData.corner2
    )
}