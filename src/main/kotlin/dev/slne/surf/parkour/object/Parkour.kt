package dev.slne.surf.parkour.`object`

import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.util.BoundingBox
import java.util.*

data class Parkour(
    val uid: UUID,
    val identifier: String,
    val displayName: String,
    val boundingBox: BoundingBox,
    val world: World,
    val startLocation: Location,
    val respawnLocation: Location
) {
    private val generators = mutableObjectSetOf<ParkourGenerator>()

    suspend fun start() {

    }

    suspend fun stop() {

    }
}