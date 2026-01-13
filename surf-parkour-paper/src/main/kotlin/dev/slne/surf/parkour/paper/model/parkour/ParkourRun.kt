package dev.slne.surf.parkour.paper.model.parkour

import java.util.*

data class ParkourRun(
    val parkour: Parkour,
    val playerUuid: UUID,
    var jumps: Int,
    var time: Long
)