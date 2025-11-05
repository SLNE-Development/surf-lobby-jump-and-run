package dev.slne.surf.parkour.config.persistence

import org.bukkit.Location
import org.bukkit.util.BoundingBox
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class SingleParkourConfig(
    val identifier: String,
    val displayName: String,
    val boundingBox: BoundingBox,
    val worldName: String,
    val startLocation: Location,
    val respawnLocation: Location
)