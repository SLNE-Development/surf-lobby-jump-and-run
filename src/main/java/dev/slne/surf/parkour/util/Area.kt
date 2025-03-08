package dev.slne.surf.parkour.util

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bukkit.util.Vector

@Serializable
data class Area (
    var max: @Contextual Vector,
    var min: @Contextual Vector
)
