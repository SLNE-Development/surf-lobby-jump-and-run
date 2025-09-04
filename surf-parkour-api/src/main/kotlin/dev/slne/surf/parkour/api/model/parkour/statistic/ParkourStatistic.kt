package dev.slne.surf.parkour.api.model.parkour.statistic

import dev.slne.surf.parkour.api.model.parkour.Parkour
import java.util.*

interface ParkourStatistic {
    val userUuid: UUID
    val parkour: Parkour
    val time: Long
    val jumps: Int
}