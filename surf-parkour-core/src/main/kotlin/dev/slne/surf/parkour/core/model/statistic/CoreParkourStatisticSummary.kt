package dev.slne.surf.parkour.core.model.statistic

import dev.slne.surf.parkour.api.model.parkour.statistic.ParkourStatisticSummary
import java.util.*

data class CoreParkourStatisticSummary(
    override val name: String,
    override val uuid: UUID,

    override val totalTries: Int,
    override val totalJumps: Int,
    override val averageTime: Int,
    override val averageJumps: Int,
    override val bestTime: Int,
    override val bestJumps: Int,
    override val worstTime: Int,
    override val worstJumps: Int,
) : ParkourStatisticSummary {
    companion object {
        fun empty() =
            CoreParkourStatisticSummary("Error", UUID.randomUUID(), 0, 0, 0, 0, 0, 0, 0, 0)
    }
}
