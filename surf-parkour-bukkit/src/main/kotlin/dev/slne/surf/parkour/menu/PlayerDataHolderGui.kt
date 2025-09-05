package dev.slne.surf.parkour.menu

import dev.slne.surf.parkour.api.model.parkour.statistic.ParkourStatisticSummary

interface PlayerDataHolderGui {
    val statistics: ParkourStatisticSummary
}