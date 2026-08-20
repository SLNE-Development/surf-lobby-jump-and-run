package dev.slne.surf.parkour.core.client.config

import dev.slne.surf.api.core.config.SpongeYmlConfigClass
import dev.slne.surf.api.core.util.mutableObjectSetOf
import dev.slne.surf.parkour.core.client.ClientParkourInstance
import dev.slne.surf.parkour.core.client.model.geometry.ParkourLocation
import dev.slne.surf.parkour.core.client.model.geometry.ParkourRegion
import dev.slne.surf.parkour.core.client.model.geometry.ParkourVector
import dev.slne.surf.parkour.core.client.model.geometry.deserializeLocation
import dev.slne.surf.parkour.core.client.model.geometry.deserializeRegion
import dev.slne.surf.parkour.core.client.model.geometry.serializeLocation
import dev.slne.surf.parkour.core.client.model.geometry.serializeRegion
import dev.slne.surf.parkour.core.client.model.parkour.Parkour
import dev.slne.surf.parkour.core.client.platform.ParkourPlatform
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
            serializeRegion(
                ParkourRegion.of(ParkourVector(9, 315, 189), ParkourVector(328, 215, 391))
            ),
            ParkourPlatform.defaultWorld,
            serializeLocation(
                ParkourLocation(ParkourPlatform.defaultWorld, 111.5, 149.0, 315.5, 90f, 0f)
            )
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
        fun toParkour(): Parkour {
            if (!ParkourPlatform.isKnownWorld(world)) error("World $world not found")

            return Parkour(
                uuid = uuid,
                identifier = identifier,
                displayName = displayName,
                boundingBox = deserializeRegion(boundingBox),
                world = world,
                respawnLocation = deserializeLocation(respawnLocation)
            )
        }
    }

    companion object : SpongeYmlConfigClass<ParkourConfig>(
        ParkourConfig::class.java,
        ClientParkourInstance.dataPath,
        "config.yml"
    ) {
        fun fromParkour(parkour: Parkour) = ConfigParkour(
            uuid = parkour.uuid,
            identifier = parkour.identifier,
            displayName = parkour.displayName,
            boundingBox = serializeRegion(parkour.boundingBox),
            world = parkour.world,
            respawnLocation = serializeLocation(parkour.respawnLocation)
        )
    }
}
