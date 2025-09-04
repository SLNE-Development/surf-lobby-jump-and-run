package dev.slne.surf.parkour.core.model.statistic

import dev.slne.surf.parkour.api.model.parkour.Parkour
import dev.slne.surf.parkour.api.model.parkour.statistic.ParkourSpecificStatistic

class CoreParkourSpecificStatistic(
    override val parkour: Parkour,
    override val tries: Int,
    override val overallJumps: Int,
    override val failures: Int,
    override val bestTry: Int

) : ParkourSpecificStatistic