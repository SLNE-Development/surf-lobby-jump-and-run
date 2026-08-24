package dev.slne.surf.parkour.core.client.service

import com.github.benmanes.caffeine.cache.Caffeine
import com.google.auto.service.AutoService
import dev.slne.surf.api.core.util.toObjectList
import dev.slne.surf.parkour.api.data.ParkourRun
import dev.slne.surf.parkour.api.data.ParkourStats
import dev.slne.surf.parkour.core.common.rabbit.packet.request.LoadAllParkourStatsRequestPacket
import dev.slne.surf.parkour.core.common.rabbit.packet.request.LoadPlayerStatsRequestPacket
import dev.slne.surf.parkour.core.common.rabbit.packet.request.SaveRunRequestPacket
import dev.slne.surf.parkour.core.client.ClientParkourInstance
import net.kyori.adventure.util.Services
import java.util.*
import kotlin.system.measureTimeMillis

@AutoService(ParkourRunsService::class)
class ParkourRunsServiceImpl : ParkourRunsService, Services.Fallback {
    private val _cachedStats = Caffeine.newBuilder()
        .build<UUID, ParkourStats>()

    override val stats get() = _cachedStats.asMap().values.toObjectList()

    override suspend fun loadAndCacheStats(playerUuid: UUID) {
        ClientParkourInstance.rabbitApi.sendRequest(LoadPlayerStatsRequestPacket(playerUuid)).stat?.let {
            _cachedStats.put(playerUuid, it)
        }
    }

    override suspend fun saveRun(run: ParkourRun) {
        _cachedStats.asMap().compute(run.playerUuid) { _, cached ->
            cached?.copy(
                totalRuns = cached.totalRuns + 1,
                totalJumps = cached.totalJumps + run.jumps,
                highscore = maxOf(cached.highscore, run.jumps),
                averageTime = ((cached.averageTime * cached.totalRuns) + run.time) /
                        (cached.totalRuns + 1)
            ) ?: ParkourStats(
                playerUuid = run.playerUuid,
                totalRuns = 1,
                totalJumps = run.jumps,
                highscore = run.jumps,
                averageTime = run.time
            )
        }

        ClientParkourInstance.rabbitApi.sendRequest(SaveRunRequestPacket(run))
    }

    override fun getStats(playerUuid: UUID): ParkourStats =
        _cachedStats.getIfPresent(playerUuid) ?: ParkourStats.empty()

    override suspend fun loadStats() = measureTimeMillis {
        val stats = ClientParkourInstance.rabbitApi
            .sendRequest(LoadAllParkourStatsRequestPacket()).stats.associateBy { it.playerUuid }

        _cachedStats.putAll(stats)
    }
}