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
            serializeBoundingBox(BoundingBox.of(Vector(9, 315, 189), Vector(328, 215, 391))),
            Bukkit.getWorlds().first().name,
            serializeLocation(Location(Bukkit.getWorlds().first(), 111.5, 149.0, 315.5, 90f, 0f))
        )
    )
) {
    @ConfigSerializable
    data class ConfigParkour(
        val uuid: UUID,
        val identifier: String,
        val displayName: String,
        val boundingBox: String,
        val world: String,
        val respawnLocation: String
    ) {
        fun toParkour() = Parkour(
            uuid = uuid,
            identifier = identifier,
            displayName = displayName,
            boundingBox = deserializeBoundingBox(boundingBox),
            world = Bukkit.getWorld(world) ?: error("World $world not found"),
            respawnLocation = deserializeLocation(respawnLocation)
        )
    }

    companion object {
        fun fromParkour(parkour: Parkour) = ConfigParkour(
            uuid = parkour.uuid,
            identifier = parkour.identifier,
            displayName = parkour.displayName,
            boundingBox = serializeBoundingBox(parkour.boundingBox),
            world = parkour.world.name,
            respawnLocation = serializeLocation(parkour.respawnLocation)
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

fun serializeLocation(loc: Location): String {
    return "${loc.world?.name},${loc.x},${loc.y},${loc.z},${loc.yaw},${loc.pitch}"
}

fun deserializeLocation(data: String): Location {
    val parts = data.split(",")
    if (parts.size != 6) error("Invalid location format")
    val world = Bukkit.getWorld(parts[0]) ?: error("World ${parts[0]} not found")
    val x = parts[1].toDouble()
    val y = parts[2].toDouble()
    val z = parts[3].toDouble()
    val yaw = parts[4].toFloat()
    val pitch = parts[5].toFloat()
    return Location(world, x, y, z, yaw, pitch)
}