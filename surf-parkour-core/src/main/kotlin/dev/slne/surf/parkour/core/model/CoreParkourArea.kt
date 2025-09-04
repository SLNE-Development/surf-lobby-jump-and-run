package dev.slne.surf.parkour.core.model

import dev.slne.surf.parkour.api.model.parkour.ParkourArea
import org.bukkit.Location
import org.bukkit.World

data class CoreParkourArea(
    override val world: World,
    override val firstLocation: Location,
    override val secondLocation: Location
) : ParkourArea
