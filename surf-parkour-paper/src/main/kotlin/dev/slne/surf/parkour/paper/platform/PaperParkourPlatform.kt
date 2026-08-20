package dev.slne.surf.parkour.paper.platform

import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mccoroutine.folia.regionDispatcher
import com.google.auto.service.AutoService
import dev.slne.surf.api.paper.glow.SurfGlowingApi
import dev.slne.surf.parkour.core.client.model.geometry.ParkourLocation
import dev.slne.surf.parkour.core.client.platform.ParkourPlatform
import dev.slne.surf.parkour.paper.plugin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.util.Services
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Registry
import org.bukkit.World
import org.bukkit.block.BlockFace
import org.bukkit.util.Vector
import java.util.*

@AutoService(ParkourPlatform::class)
class PaperParkourPlatform : ParkourPlatform, Services.Fallback {

    override val defaultWorld: String get() = Bukkit.getWorlds().first().name

    override fun isKnownWorld(world: String) = Bukkit.getWorld(world) != null

    override fun audience(playerUuid: UUID) = Bukkit.getPlayer(playerUuid)

    override fun playerName(playerUuid: UUID) = Bukkit.getPlayer(playerUuid)?.name

    override fun skinTexture(playerUuid: UUID) = Bukkit.getPlayer(playerUuid)
        ?.playerProfile
        ?.properties
        ?.find { it.name == "textures" }
        ?.value

    override fun yaw(playerUuid: UUID) = Bukkit.getPlayer(playerUuid)?.location?.yaw

    override fun launch(block: suspend CoroutineScope.() -> Unit) = plugin.launch { block() }

    override fun launchAsync(block: suspend CoroutineScope.() -> Unit) =
        plugin.launch(Dispatchers.IO) { block() }

    override suspend fun setBlock(location: ParkourLocation, type: Key) {
        val bukkitLocation = location.toBukkit() ?: return

        withContext(plugin.regionDispatcher(bukkitLocation)) {
            val data = Registry.BLOCK
                .getOrThrow(type)
                .createBlockData()

            bukkitLocation.world.setBlockData(bukkitLocation, data)
        }
    }

    override suspend fun hasFreeSpaceAbove(location: ParkourLocation, height: Int): Boolean {
        val bukkitLocation = location.toBukkit() ?: return false

        return withContext(plugin.regionDispatcher(bukkitLocation)) {
            var block = bukkitLocation.world.getBlockAt(bukkitLocation)

            repeat(height) {
                block = block.getRelative(BlockFace.UP)
                if (!block.isEmpty) return@withContext false
            }

            true
        }
    }

    override fun teleport(playerUuid: UUID, location: ParkourLocation) {
        val player = Bukkit.getPlayer(playerUuid) ?: return
        val bukkitLocation = location.toBukkit() ?: return

        player.teleportAsync(bukkitLocation)
    }

    override fun resetVelocity(playerUuid: UUID) {
        Bukkit.getPlayer(playerUuid)?.velocity = Vector(0, 0, 0)
    }

    override fun highlightBlock(
        playerUuid: UUID,
        location: ParkourLocation,
        color: NamedTextColor
    ) {
        val player = Bukkit.getPlayer(playerUuid) ?: return
        val bukkitLocation = location.toBukkit() ?: return

        SurfGlowingApi.makeGlowing(bukkitLocation, player, color)
    }

    override fun clearHighlight(playerUuid: UUID, location: ParkourLocation) {
        val player = Bukkit.getPlayer(playerUuid) ?: return
        val bukkitLocation = location.toBukkit() ?: return

        SurfGlowingApi.removeGlowing(bukkitLocation, player)
    }
}

private fun ParkourLocation.toBukkit(): Location? {
    val world: World = Bukkit.getWorld(world) ?: return null
    return Location(world, x, y, z, yaw, pitch)
}