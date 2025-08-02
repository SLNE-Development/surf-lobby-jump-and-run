package dev.slne.surf.parkour.core.model

import dev.slne.surf.parkour.api.entity.ParkourPlayer
import dev.slne.surf.parkour.api.model.Parkour
import dev.slne.surf.parkour.api.model.ParkourGenerator
import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.*

class CoreParkour(
    override val uuid: UUID,
    override val name: String,
    override val players: ObjectSet<UUID>,
    override val generator: Object2ObjectMap<UUID, ParkourGenerator>
) : Parkour {
    override fun start(player: ParkourPlayer) {

    }
}