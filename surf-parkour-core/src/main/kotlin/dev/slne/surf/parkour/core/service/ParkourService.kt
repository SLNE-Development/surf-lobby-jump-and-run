package dev.slne.surf.parkour.core.service

import dev.slne.surf.parkour.api.entity.ParkourPlayer
import dev.slne.surf.parkour.api.model.parkour.Parkour
import dev.slne.surf.parkour.api.model.parkour.ParkourCreationData
import dev.slne.surf.parkour.core.util.TableHolder
import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.*

interface ParkourService : TableHolder {
    fun createParkour(parkourData: ParkourCreationData): Parkour
    fun deleteParkour(parkour: Parkour)
    fun getParkour(player: ParkourPlayer): Parkour?
    fun getParkour(name: String): Parkour?
    fun getParkour(uuid: UUID): Parkour?
    fun inParkour(player: ParkourPlayer): Boolean
    fun getParkours(): ObjectSet<Parkour>

    suspend fun pushParkour(parkour: Parkour, serverUuid: UUID): Unit
    suspend fun fetchParkours(serverUuid: UUID)

    suspend fun onFailure(parkour: Parkour, player: ParkourPlayer)
    suspend fun onSuccess(parkour: Parkour, player: ParkourPlayer, index: Int)

    companion object {
        val INSTANCE = requiredService<ParkourService>()
    }
}

val parkourService get() = ParkourService.INSTANCE