package dev.slne.surf.parkour.api.model.parkour

import dev.slne.surf.parkour.api.entity.ParkourPlayer
import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import it.unimi.dsi.fastutil.objects.ObjectSet
import org.bukkit.Location
import java.util.*

interface Parkour {
    val uuid: UUID
    val name: String
    val spawnLocation: Location
    val area: ParkourArea

    val players: ObjectSet<UUID>
    val generators: Object2ObjectMap<UUID, ParkourGenerator>
    val playerTimes: Object2ObjectMap<UUID, Long>

    suspend fun start(player: ParkourPlayer)
    suspend fun onFailure(player: ParkourPlayer)
    suspend fun onSuccess(player: ParkourPlayer, index: Int)

    fun modify(block: Parkour.() -> Unit)
    fun modifySaving(block: Parkour.() -> Unit)
}