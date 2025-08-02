package dev.slne.surf.parkour.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class GeneralParkourConfig(
    val betaMode: Boolean
)
