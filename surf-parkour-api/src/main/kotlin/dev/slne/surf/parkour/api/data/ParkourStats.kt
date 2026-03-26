package dev.slne.surf.parkour.api.data

import dev.slne.surf.surfapi.core.api.serializer.java.uuid.SerializableUUID
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class ParkourStats(
    val playerUuid: SerializableUUID,
    val totalRuns: Int,
    val totalJumps: Int,
    val highscore: Int,
    val averageTime: Long,
) {
    companion object {
        fun empty() = ParkourStats(
            playerUuid = UUID.randomUUID(),
            totalRuns = -1,
            totalJumps = -1,
            highscore = -1,
            averageTime = -1L
        )
    }
}