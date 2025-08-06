package dev.slne.surf.parkour.core.model.statistic

import dev.slne.surf.parkour.api.model.parkour.Parkour
import dev.slne.surf.parkour.api.model.parkour.statistic.ParkourSpecificStatistic

class CoreParkourSpecificStatistic(
    override val parkour: Parkour,
    override val jumpCount: Int,
    override val recordCount: Int,
    override val triesCount: Int
) : ParkourSpecificStatistic