package dev.slne.surf.parkour.paper.config

import dev.slne.surf.parkour.paper.model.parkour.Parkour
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class ParkourConfig(
    val betaMode: Boolean = false,
    val parkours: MutableSet<Parkour>
)
