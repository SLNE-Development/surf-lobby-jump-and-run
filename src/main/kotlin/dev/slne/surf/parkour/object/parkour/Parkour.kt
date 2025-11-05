package dev.slne.surf.parkour.`object`.parkour

import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.util.BoundingBox
import java.util.*

data class Parkour(
    val identifier: String,
    val displayName: String,
    val boundingBox: BoundingBox,
    val world: World,
    val startLocation: Location,
    val respawnLocation: Location
) {
    val generators = mutableObjectSetOf<ParkourGenerator>()
    val players = mutableObjectSetOf<UUID>()

    suspend fun start(player: UUID) {
        val generator = ParkourGenerator(player, this, Material.RED_CONCRETE)

        players.add(player)
        generators.add(generator)

        generator.start()
    }

    fun getGenerator(player: UUID) = generators.find { it.associatedPlayer == player }
    fun getGenerator(player: Player) = getGenerator(player.uniqueId)

    suspend fun exit(player: UUID) {
        val generator = generators.find { it.associatedPlayer == player } ?: return

        generator.stop()
        generators.remove(generator)
        players.remove(player)

        val player = Bukkit.getPlayer(player) ?: return
        player.teleportAsync(respawnLocation)
    }
}