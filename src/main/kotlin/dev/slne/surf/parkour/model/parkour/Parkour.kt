package dev.slne.surf.parkour.model.parkour

import dev.slne.surf.parkour.service.parkourService
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.util.BoundingBox
import java.util.*

data class Parkour(
    val uuid: UUID,
    val identifier: String,
    val displayName: String,
    val boundingBox: BoundingBox,
    val world: World,
    val respawnLocation: Location
) {
    val generators = mutableObjectSetOf<ParkourGenerator>()
    val players = mutableObjectSetOf<UUID>()

    private val concretes = listOf(
        Material.RED_CONCRETE,
        Material.BLUE_CONCRETE,
        Material.GREEN_CONCRETE,
        Material.YELLOW_CONCRETE,
        Material.ORANGE_CONCRETE,
        Material.PURPLE_CONCRETE,
        Material.CYAN_CONCRETE,
        Material.MAGENTA_CONCRETE
    )

    private val playerMaterials = mapOf(
        UUID.fromString("1c779cb1-3860-4e23-9cac-7f160b2acc61") to Material.RED_CONCRETE, // TheBjoRedCraft
        UUID.fromString("3094f2ed-fe51-46fd-a1dc-d76e846548e5") to Material.FLOWERING_AZALEA_LEAVES // Floweryalina
    )

    fun getBlockMaterial(player: UUID) = playerMaterials[player] ?: concretes.random()

    suspend fun start(player: UUID) {
        val generator = ParkourGenerator(player, this, getBlockMaterial(player))

        players.add(player)
        generators.add(generator)

        generator.start()
    }

    fun getGenerator(player: UUID) = generators.find { it.associatedPlayer == player }
    fun getGenerator(player: Player) = getGenerator(player.uniqueId)

    fun getCurrentIndex(player: UUID) = getGenerator(player)?.currentIndex ?: 0

    suspend fun processRun(player: UUID): Int? {
        val generator = generators.find { it.associatedPlayer == player } ?: return null
        val highscore = parkourService.getRuns(player).maxByOrNull { it.jumps }

        parkourService.addRun(
            ParkourRun(
                this,
                player,
                generator.currentIndex,
                System.currentTimeMillis() - generator.startTime
            )
        )

        if (highscore != null && highscore.jumps < generator.currentIndex) {
            return generator.currentIndex
        }

        return null
    }

    suspend fun exit(player: UUID) {
        val generator = generators.find { it.associatedPlayer == player } ?: return

        generator.stop()
        generators.remove(generator)

        val player = Bukkit.getPlayer(player) ?: return

        player.teleportAsync(respawnLocation)
    }

    fun preExit(player: UUID) {
        players.remove(player)
    }

    companion object {
        fun all() = parkourService.getParkours()
    }
}
