package dev.slne.surf.parkour.core.model.statistic

import dev.slne.surf.parkour.api.model.parkour.statistic.ParkourStatistic

class CoreParkourStatistic(
    override val tries: Int,
    override val overallJumps: Int,
    override val failures: Int,
    override val bestTry: Int
) : ParkourStatistic {
    companion object {
        fun empty(): CoreParkourStatistic {
            return CoreParkourStatistic(0, 0, 0, 0)
        }

        fun of(tries: Int, overallJumps: Int, failures: Int, bestTry: Int): CoreParkourStatistic {
            return CoreParkourStatistic(tries, overallJumps, failures, bestTry)
        }
    }
}