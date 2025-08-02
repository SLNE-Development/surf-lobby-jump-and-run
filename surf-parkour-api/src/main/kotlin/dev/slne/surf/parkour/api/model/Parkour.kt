package dev.slne.surf.parkour.api.model

import dev.slne.surf.parkour.api.entity.ParkourPlayer
import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.*

interface Parkour {
    val uuid: UUID
    val name: String

    val players: ObjectSet<UUID>
    val generator: Object2ObjectMap<UUID, ParkourGenerator>

    fun start(player: ParkourPlayer)
}