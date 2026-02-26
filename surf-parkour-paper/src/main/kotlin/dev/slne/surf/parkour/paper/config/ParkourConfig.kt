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
    val parkours: MutableSet<Parkour> = mutableObjectSetOf(
        Parkour(
            UUID.randomUUID(),
            "lobby",
            "Lobby",
            BoundingBox.of(Vector(4, 324, 189), Vector(328, 215, 391)),
            Bukkit.getWorlds().first(),
            Location(Bukkit.getWorlds().first(), 111.5, 149.0, 315.5, 90f, 0f)
        )
    )
)
