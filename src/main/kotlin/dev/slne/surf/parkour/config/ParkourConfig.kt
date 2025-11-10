package dev.slne.surf.parkour.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import java.util.*

@ConfigSerializable
data class ParkourConfig(
    val betaMode: Boolean = false,
    val serverUuid: UUID = UUID.randomUUID()
)
