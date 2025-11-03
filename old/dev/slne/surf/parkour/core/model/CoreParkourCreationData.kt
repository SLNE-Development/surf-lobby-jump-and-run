package dev.slne.surf.parkour.core.model

import dev.slne.surf.parkour.api.model.parkour.ParkourCreationData
import org.bukkit.Location

data class CoreParkourCreationData(
    override val name: String,
    override val corner1: Location,
    override val corner2: Location,
    override val spawn: Location
) : ParkourCreationData
