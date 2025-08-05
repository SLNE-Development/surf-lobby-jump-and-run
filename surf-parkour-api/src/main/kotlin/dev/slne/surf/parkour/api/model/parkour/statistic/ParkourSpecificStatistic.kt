package dev.slne.surf.parkour.api.model.parkour.statistic

import dev.slne.surf.parkour.api.model.parkour.Parkour

interface ParkourSpecificStatistic : ParkourStatistic {
    val parkour: Parkour
}