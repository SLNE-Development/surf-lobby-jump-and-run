package dev.slne.surf.parkour.core.client.platform

import dev.slne.surf.api.core.util.requiredService
import dev.slne.surf.parkour.core.client.model.geometry.ParkourLocation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.format.NamedTextColor
import java.util.*

private val platform = requiredService<ParkourPlatform>()

/**
 * The platform-specific operations the shared parkour logic relies on.
 *
 * Every platform contributes exactly one implementation through `ServiceLoader`.
 */
interface ParkourPlatform {

    /**
     * The name of the world a newly written config falls back to.
     */
    val defaultWorld: String

    /**
     * Returns whether a world named [world] is loaded on this server.
     */
    fun isKnownWorld(world: String): Boolean

    /**
     * Returns the player identified by [playerUuid] as an audience, or `null` while they are not
     * connected to this server.
     */
    fun audience(playerUuid: UUID): Audience?

    /**
     * Returns the name of the player identified by [playerUuid], or `null` while they are not
     * connected to this server.
     */
    fun playerName(playerUuid: UUID): String?

    /**
     * Returns the encoded skin textures of the player identified by [playerUuid], or `null` while
     * they are not connected to this server or carry no skin.
     */
    fun skinTexture(playerUuid: UUID): String?

    /**
     * Returns the yaw the player identified by [playerUuid] is looking at, or `null` while they are
     * not connected to this server.
     */
    fun yaw(playerUuid: UUID): Float?

    /**
     * Launches [block] on the context the platform runs game logic on.
     */
    fun launch(block: suspend CoroutineScope.() -> Unit): Job

    /**
     * Launches [block] off the context the platform runs game logic on.
     */
    fun launchAsync(block: suspend CoroutineScope.() -> Unit): Job

    /**
     * Replaces the block at [location] with [type].
     */
    suspend fun setBlock(location: ParkourLocation, type: Key)

    /**
     * Returns whether the [height] blocks above [location] are all free, so a player can stand on
     * the block [location] lies in.
     */
    suspend fun hasFreeSpaceAbove(location: ParkourLocation, height: Int): Boolean

    /**
     * Moves the player identified by [playerUuid] to [location].
     */
    fun teleport(playerUuid: UUID, location: ParkourLocation)

    /**
     * Stops the player identified by [playerUuid] from moving any further.
     */
    fun resetVelocity(playerUuid: UUID)

    /**
     * Outlines the block at [location] in [color] for the player identified by [playerUuid] only.
     */
    fun highlightBlock(playerUuid: UUID, location: ParkourLocation, color: NamedTextColor)

    /**
     * Removes an outline [highlightBlock] added.
     */
    fun clearHighlight(playerUuid: UUID, location: ParkourLocation)

    companion object : ParkourPlatform by platform {
        val INSTANCE get() = platform
    }
}
