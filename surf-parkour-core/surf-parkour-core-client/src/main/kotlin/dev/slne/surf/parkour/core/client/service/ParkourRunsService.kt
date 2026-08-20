package dev.slne.surf.parkour.core.client.service

import dev.slne.surf.api.core.util.requiredService
import dev.slne.surf.parkour.api.data.ParkourRun
import dev.slne.surf.parkour.api.data.ParkourStats
import it.unimi.dsi.fastutil.objects.ObjectList
import org.jetbrains.annotations.UnmodifiableView
import java.util.*

private val service = requiredService<ParkourRunsService>()

interface ParkourRunsService {
    val stats: @UnmodifiableView ObjectList<ParkourStats>

    suspend fun loadAndCacheStats(playerUuid: UUID)
    suspend fun saveRun(run: ParkourRun)

    fun getStats(playerUuid: UUID): ParkourStats

    suspend fun loadStats(): Long

    companion object : ParkourRunsService by service {
        val INSTANCE get() = service
    }
}