package dev.slne.surf.parkour.core.client.service

import dev.slne.surf.parkour.core.client.model.geometry.ParkourLocation
import dev.slne.surf.parkour.core.client.model.geometry.ParkourRegion
import dev.slne.surf.parkour.core.client.model.parkour.Parkour
import it.unimi.dsi.fastutil.objects.ObjectSet
import org.jetbrains.annotations.UnmodifiableView
import java.util.*

interface ParkourService {
    val parkours: @UnmodifiableView ObjectSet<Parkour>

    fun createParkour(
        uuid: UUID,
        identifier: String,
        displayName: String,
        boundingBox: ParkourRegion,
        world: String,
        respawnLocation: ParkourLocation
    ): Parkour

    fun triggerFailure(playerUuid: UUID)
    fun triggerSuccess(playerUuid: UUID)

    fun isInParkour(playerUuid: UUID): Boolean

    fun getParkourByPlayer(playerUuid: UUID): Parkour?
    fun getParkour(parkourUuid: UUID): Parkour?
    fun getParkour(identifier: String): Parkour?

    fun exists(identifier: String): Boolean

    fun loadParkours()

    companion object : ParkourService by ParkourServiceImpl
}
