package dev.slne.surf.lobby.jar.player

import com.github.benmanes.caffeine.cache.Caffeine
import dev.hsbrysk.caffeine.CoroutineLoadingCache
import dev.hsbrysk.caffeine.buildCoroutine
import java.util.*
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

object JumpAndRunPlayerManager {

    private val players: CoroutineLoadingCache<UUID, JumpAndRunPlayer> =
        Caffeine.newBuilder().expireAfterAccess(120.minutes.toJavaDuration())
            .buildCoroutine {
                val player = JumpAndRunPlayer(it)
                player.fetch()

                player
            }

    suspend fun get(uuid: UUID) = players.get(uuid)!!

}