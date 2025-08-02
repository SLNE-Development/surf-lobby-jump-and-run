package dev.slne.surf.parkour.core.factory

import dev.slne.surf.parkour.api.model.Parkour
import dev.slne.surf.parkour.api.model.ParkourCreationData
import dev.slne.surf.surfapi.core.api.util.requiredService

interface ParkourFactory {
    fun createParkour(parkourData: ParkourCreationData): Parkour

    companion object {
        val INSTANCE = requiredService<ParkourFactory>()
    }
}

val parkourFactory get() = ParkourFactory.INSTANCE