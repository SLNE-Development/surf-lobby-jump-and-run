package dev.slne.surf.parkour.paper.model.parkour

import dev.slne.surf.parkour.api.data.ParkourStats
import java.util.*

data class ParkourRun(
    val parkour: Parkour,
    val playerUuid: UUID,
    var jumps: Int,
    var time: Long
) {

    fun addTo(stats: ParkourStats) = stats.copy(
        totalRuns = stats.totalRuns + 1,
        totalJumps = stats.totalJumps + jumps,
        highscore = maxOf(stats.highscore, jumps),
        averageTime = if (stats.averageTime < 0) time else (stats.averageTime + time) / 2
    )
}