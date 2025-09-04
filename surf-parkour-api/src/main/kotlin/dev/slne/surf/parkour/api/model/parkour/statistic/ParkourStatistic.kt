package dev.slne.surf.parkour.api.model.parkour.statistic

interface ParkourStatistic {
    val tries: Int
    val overallJumps: Int
    val failures: Int
    val bestTry: Int
}