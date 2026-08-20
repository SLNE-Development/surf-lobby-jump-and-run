package dev.slne.surf.parkour.minestom.platform

import com.google.auto.service.AutoService
import dev.slne.minestom.lobby.api.coroutine.minestomAsyncScope
import dev.slne.minestom.lobby.api.coroutine.minestomScope
import dev.slne.minestom.lobby.api.extension.ConnectionManager
import dev.slne.minestom.lobby.api.extension.getOrThrow
import dev.slne.minestom.lobby.api.highlight.BlockHighlights
import dev.slne.surf.parkour.core.client.model.geometry.ParkourLocation
import dev.slne.surf.parkour.core.client.platform.ParkourPlatform
import dev.slne.surf.parkour.minestom.ParkourMinestomEntrypoint
import dev.slne.surf.parkour.minestom.instance.findParkourInstance
import dev.slne.surf.parkour.minestom.instance.instance
import dev.slne.surf.parkour.minestom.instance.parkourWorldName
import dev.slne.surf.parkour.minestom.instance.toPos
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Player
import net.minestom.server.instance.block.Block
import java.util.*

@AutoService(ParkourPlatform::class)
class MinestomParkourPlatform : ParkourPlatform {

    override val defaultWorld: String
        get() = ParkourMinestomEntrypoint.lobbyInstance.parkourWorldName ?: FALLBACK_WORLD

    override fun isKnownWorld(world: String) = findParkourInstance(world) != null

    override fun audience(playerUuid: UUID) = player(playerUuid)

    override fun playerName(playerUuid: UUID) = player(playerUuid)?.username

    override fun skinTexture(playerUuid: UUID) = player(playerUuid)?.skin?.textures()

    override fun yaw(playerUuid: UUID) = player(playerUuid)?.position?.yaw()

    override fun launch(block: suspend CoroutineScope.() -> Unit) =
        minestomScope.launch(block = block)

    override fun launchAsync(block: suspend CoroutineScope.() -> Unit) =
        minestomAsyncScope.launch(block = block)

    override suspend fun setBlock(location: ParkourLocation, type: Key) {
        val instance = location.instance() ?: return
        val pos = location.toPos()
        val block = Block
            .staticRegistry()
            .getOrThrow(type)

        instance.setBlock(pos, block)
    }

    override suspend fun hasFreeSpaceAbove(location: ParkourLocation, height: Int): Boolean {
        val instance = location.instance() ?: return false
        val position = location.toPos()

        if (!instance.isChunkLoaded(position)) return false

        for (y in 1..height) {
            val point = position.add(0.0, y.toDouble(), 0.0)
            val block = instance.getBlock(point)
            if (!block.air()) return false
        }

        return true
    }

    override fun teleport(playerUuid: UUID, location: ParkourLocation) {
        val player = player(playerUuid) ?: return
        val instance = location.instance() ?: return

        if (player.instance != instance) {
            player.setInstance(instance, location.toPos())
            return
        }

        player.teleport(location.toPos())
    }

    override fun resetVelocity(playerUuid: UUID) {
        player(playerUuid)?.velocity = Vec.ZERO
    }

    override fun highlightBlock(
        playerUuid: UUID,
        location: ParkourLocation,
        color: NamedTextColor
    ) {
        val player = player(playerUuid) ?: return
        val instance = location.instance() ?: return

        BlockHighlights.show(player, instance, location.toPos(), color)
    }

    override fun clearHighlight(playerUuid: UUID, location: ParkourLocation) {
        val player = player(playerUuid) ?: return
        val instance = location.instance() ?: return

        BlockHighlights.hide(player, instance, location.toPos())
    }

    private fun player(playerUuid: UUID): Player? =
        ConnectionManager.getOnlinePlayerByUuid(playerUuid)

    private companion object {
        const val FALLBACK_WORLD = "lobby"
    }
}