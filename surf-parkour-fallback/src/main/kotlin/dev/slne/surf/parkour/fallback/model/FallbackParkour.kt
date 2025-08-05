package dev.slne.surf.parkour.fallback.model

import dev.slne.surf.parkour.api.entity.ParkourPlayer
import dev.slne.surf.parkour.api.model.Parkour
import dev.slne.surf.parkour.api.model.ParkourGenerator
import dev.slne.surf.parkour.fallback.generator.DefaultParkourGenerator
import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import it.unimi.dsi.fastutil.objects.ObjectSet
import org.bukkit.Location
import org.bukkit.Material
import java.util.*

class FallbackParkour(
    override val uuid: UUID,
    override val name: String,
    override val players: ObjectSet<ParkourPlayer>,
    override val generator: Object2ObjectMap<UUID, ParkourGenerator>,
    override val spawnLocation: Location,
    override val corner1: Location,
    override val corner2: Location
) : Parkour {
    override suspend fun start(player: ParkourPlayer) {
        val generator = DefaultParkourGenerator(
            player,
            Material.RED_CONCRETE,
            corner1,
            corner2,
            spawnLocation
        )

        this@FallbackParkour.generator[player.uuid] = generator
        generator.start()
    }

    override suspend fun onFailure(player: ParkourPlayer) {
        TODO("Not yet implemented")
    }

    override suspend fun onSuccess(player: ParkourPlayer) {
        TODO("Not yet implemented")
    }
}