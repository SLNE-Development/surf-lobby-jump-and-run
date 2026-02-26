package dev.slne.surf.parkour.paper.config

import dev.slne.surf.parkour.paper.model.parkour.Parkour
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.util.BoundingBox
import org.bukkit.util.Vector
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import java.util.*

@ConfigSerializable
data class ParkourConfig(
    val betaMode: Boolean = false,
    val parkours: MutableSet<ConfigParkour> = mutableObjectSetOf(
        ConfigParkour(
            UUID.fromString("3f9c2e1a-7b84-4d6e-a9f2-1c5b8e73d4a1"),
            "lobby",
            "Lobby",
            serializeBoundingBox(BoundingBox.of(Vector(4, 324, 189), Vector(328, 215, 391))),
            Bukkit.getWorlds().first().name,
            Location(Bukkit.getWorlds().first(), 111.5, 149.0, 315.5, 90f, 0f)
        )
    )
) {
    data class ConfigParkour(
        val uuid: UUID,
        val identifier: String,
        val displayName: String,
        val boundingBox: String,
        val world: String,
        val respawnLocation: Location
    ) {
        fun toParkour() = Parkour(
            uuid = uuid,
            identifier = identifier,
            displayName = displayName,
            boundingBox = deserializeBoundingBox(boundingBox),
            world = Bukkit.getWorld(world) ?: error("World $world not found"),
            respawnLocation = respawnLocation
        )
    }

    companion object {
        fun fromParkour(parkour: Parkour) = ConfigParkour(
            uuid = parkour.uuid,
            identifier = parkour.identifier,
            displayName = parkour.displayName,
            boundingBox = serializeBoundingBox(parkour.boundingBox),
            world = parkour.world.name,
            respawnLocation = parkour.respawnLocation
        )
    }
}

fun serializeBoundingBox(box: BoundingBox): String {
    val min = box.min
    val max = box.max
    return "${min.x},${min.y},${min.z};${max.x},${max.y},${max.z}"
}

fun deserializeBoundingBox(data: String): BoundingBox {
    val parts = data.split(";")
    if (parts.size != 2) error("Invalid bounding box format")

    val minParts = parts[0].split(",").map { it.toDouble() }
    val maxParts = parts[1].split(",").map { it.toDouble() }

    if (minParts.size != 3 || maxParts.size != 3) error("Invalid bounding box format")

    val min = Vector(minParts[0], minParts[1], minParts[2])
    val max = Vector(maxParts[0], maxParts[1], maxParts[2])

    return BoundingBox.of(min, max)
}
