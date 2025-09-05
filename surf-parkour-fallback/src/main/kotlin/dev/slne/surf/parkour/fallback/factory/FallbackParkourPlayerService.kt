package dev.slne.surf.parkour.fallback.factory

import com.github.benmanes.caffeine.cache.Caffeine
import com.google.auto.service.AutoService
import com.sksamuel.aedile.core.asLoadingCache
import com.sksamuel.aedile.core.expireAfterWrite
import dev.slne.surf.parkour.api.entity.ParkourPlayer
import dev.slne.surf.parkour.core.entity.CoreParkourPlayer
import dev.slne.surf.parkour.core.factory.parkourPlayerFactory
import dev.slne.surf.parkour.core.service.ParkourPlayerService
import dev.slne.surf.parkour.core.util.loadProfileTexture
import dev.slne.surf.parkour.fallback.entity.ParkourPlayerEntity
import dev.slne.surf.parkour.fallback.table.ParkourPlayerTable
import kotlinx.coroutines.Dispatchers
import net.kyori.adventure.util.Services
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.*
import kotlin.time.Duration.Companion.hours

@AutoService(ParkourPlayerService::class)
class FallbackParkourPlayerService : ParkourPlayerService, Services.Fallback {
    private val nameToPlayerCache = Caffeine.newBuilder()
        .expireAfterWrite(12.hours)
        .asLoadingCache<String, ParkourPlayer>(::loadPlayer)

    private val uuidToPlayerCache = Caffeine.newBuilder()
        .expireAfterWrite(12.hours)
        .asLoadingCache<UUID, ParkourPlayer>(::loadPlayer)

    override suspend fun loadPlayer(uuid: UUID) = newSuspendedTransaction(Dispatchers.IO) {
        val texture = loadProfileTexture(uuid)
        ParkourPlayerEntity.find(ParkourPlayerTable.uuid eq uuid).map {
            CoreParkourPlayer(
                it.uuid,
                it.name,
                texture
            )
        }.firstOrNull() ?: parkourPlayerFactory.createPlayer(uuid)
    }

    override suspend fun loadPlayer(name: String) = newSuspendedTransaction(Dispatchers.IO) {
        ParkourPlayerEntity.find(ParkourPlayerTable.name eq name).map {
            val texture = loadProfileTexture(it.uuid)
            CoreParkourPlayer(
                it.uuid,
                it.name,
                texture
            )
        }.firstOrNull() ?: parkourPlayerFactory.createPlayer(name)
    }

    override suspend fun insertPlayer(player: ParkourPlayer) =
        newSuspendedTransaction(Dispatchers.IO) {
            ParkourPlayerEntity.new {
                uuid = player.uuid
                name = player.name
                profileTexture = player.profileTexture
            }
            return@newSuspendedTransaction
        }

    override suspend fun getPlayer(uuid: UUID) = uuidToPlayerCache.get(uuid)
    override suspend fun getPlayer(name: String) = nameToPlayerCache.get(name)
}