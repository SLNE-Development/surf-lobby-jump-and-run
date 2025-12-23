package dev.slne.surf.parkour.model.parkour

import dev.slne.surf.parkour.service.parkourService
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
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

    suspend fun start(player: UUID) {
        val generator = ParkourGenerator(player, this, Material.RED_CONCRETE)

        players.add(player)
        generators.add(generator)

        Bukkit.getPlayer(player)?.addPotionEffect(
            PotionEffect(
                PotionEffectType.INVISIBILITY,
                PotionEffect.INFINITE_DURATION,
                0,
                false,
                false
            )
        )

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

        player.removePotionEffect(PotionEffectType.INVISIBILITY)
        player.teleportAsync(respawnLocation)
    }

    fun preExit(player: UUID) {
        players.remove(player)
    }

    companion object {
        fun all() = parkourService.getParkours()
    }
}
