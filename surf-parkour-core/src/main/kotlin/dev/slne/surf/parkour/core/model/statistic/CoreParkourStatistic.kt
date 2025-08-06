package dev.slne.surf.parkour.core.model.statistic

import dev.slne.surf.parkour.api.model.parkour.statistic.ParkourStatistic

class CoreParkourStatistic(
    override val jumpCount: Int,
    override val recordCount: Int,
    override val triesCount: Int
) : ParkourStatistic {
    companion object {
        fun empty(): CoreParkourStatistic {
            return CoreParkourStatistic(0, 0, 0)
        }

        fun of(jumpCount: Int, recordCount: Int, triesCount: Int): CoreParkourStatistic {
            return CoreParkourStatistic(jumpCount, recordCount, triesCount)
        }
    }
}