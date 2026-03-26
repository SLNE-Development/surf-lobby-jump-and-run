package dev.slne.surf.parkour.api.data

import dev.slne.surf.surfapi.core.api.serializer.java.uuid.SerializableUUID
import kotlinx.serialization.Serializable

@Serializable
data class ParkourRun(
    val parkourUuid: SerializableUUID,
    val playerUuid: SerializableUUID,
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