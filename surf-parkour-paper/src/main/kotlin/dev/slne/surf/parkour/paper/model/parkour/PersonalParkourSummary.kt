package dev.slne.surf.parkour.paper.model.parkour

import dev.slne.surf.parkour.api.data.ParkourRun
import dev.slne.surf.surfapi.core.api.service.PlayerLookupService
import it.unimi.dsi.fastutil.objects.ObjectList
import java.util.*

data class PersonalParkourSummary(
    val uuid: UUID,
    val runs: ObjectList<ParkourRun>,
    val playerName: String? = null
) {
    private var _name: String? = playerName

    suspend fun initName() = _name ?: PlayerLookupService.getUsername(uuid).also {
        _name = it
    }

    val name get() = _name ?: "Unknown"

    val totalTries = runs.size
    val totalJumps = runs.sumOf { it.jumps }
    val averageTime = if (runs.isEmpty()) 0 else runs.sumOf { it.time.toInt() } / runs.size
    val averageJumps = if (runs.isEmpty()) 0 else runs.sumOf { it.jumps } / runs.size
    val bestTime = runs.maxOfOrNull { it.time.toInt() } ?: 0
    val bestJumps = runs.maxOfOrNull { it.jumps } ?: 0
    val worstTime = runs.minOfOrNull { it.time.toInt() } ?: 0
    val worstJumps = runs.minOfOrNull { it.jumps } ?: 0
}