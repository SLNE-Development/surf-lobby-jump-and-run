package dev.slne.surf.parkour.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import java.util.*

@ConfigSerializable
data class ParkourConfig(
    val serverUuid: UUID = UUID.randomUUID(),
    val betaMode: Boolean = false,
)
