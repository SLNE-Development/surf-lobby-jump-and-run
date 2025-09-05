package dev.slne.surf.parkour.core.model.statistic

import dev.slne.surf.parkour.api.model.parkour.Parkour
import dev.slne.surf.parkour.api.model.parkour.statistic.ParkourStatistic
import java.util.*

data class CoreParkourStatistic(
    override val userUuid: UUID,
    override val parkour: Parkour,
    override val time: Long,
    override val jumps: Int
) : ParkourStatistic {
    companion object {
        fun empty(userUuid: UUID, parkour: Parkour) = CoreParkourStatistic(userUuid, parkour, 0, 0)
    }
}