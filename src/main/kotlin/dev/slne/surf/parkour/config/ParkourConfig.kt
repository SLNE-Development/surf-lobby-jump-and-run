package dev.slne.surf.parkour.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class ParkourConfig(
    val betaMode: Boolean = false,
)
