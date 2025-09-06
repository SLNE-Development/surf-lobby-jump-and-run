package dev.slne.surf.parkour.api.model.parkour.statistic

import java.util.*

interface ParkourStatisticSummary {
    val name: String
    val uuid: UUID

    val totalTries: Int
    val totalJumps: Int
    val averageTime: Int
    val averageJumps: Int
    val bestTime: Int
    val bestJumps: Int
    val worstTime: Int
    val worstJumps: Int
}