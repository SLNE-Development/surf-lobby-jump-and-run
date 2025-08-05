package dev.slne.surf.parkour.api.model

import dev.slne.surf.parkour.api.entity.ParkourPlayer
import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import it.unimi.dsi.fastutil.objects.ObjectSet
import org.bukkit.Location
import java.util.*

interface Parkour {
    val uuid: UUID
    val name: String
    val spawnLocation: Location
    val corner1: Location
    val corner2: Location

    val players: ObjectSet<ParkourPlayer>
    val generator: Object2ObjectMap<UUID, ParkourGenerator>

    suspend fun start(player: ParkourPlayer)
    suspend fun onFailure(player: ParkourPlayer)
    suspend fun onSuccess(player: ParkourPlayer)
}