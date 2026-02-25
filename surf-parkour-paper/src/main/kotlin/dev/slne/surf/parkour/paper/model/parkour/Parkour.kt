package dev.slne.surf.parkour.paper.model.parkour

import dev.slne.surf.parkour.paper.service.parkourService
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
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
    val waitPlease = mutableObjectSetOf<UUID>()

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

    suspend fun start(player: UUID): Boolean {
        val generator = ParkourGenerator(player, this@Parkour, getBlockMaterial(player))

        if (parkourService.isInParkour(player)) {
            Bukkit.getPlayer(player)?.let {
                it.sendText {
                    appendErrorPrefix()
                    error("Du bist bereits in einem Parkour!")
                }
            }
            return false
        }

        if (waitPlease.contains(player)) {
            Bukkit.getPlayer(player)?.let {
                it.sendText {
                    appendErrorPrefix()
                    error("Bitte warte einen Moment, bevor du den Parkour erneut betrittst.")
                }
            }
            return false
        }

        players.add(player)
        generators.add(generator)

        generator.start()

        return true
    }

    fun getGenerator(player: UUID) = generators.find { it.associatedPlayer == player }
    fun getGenerator(player: Player) = getGenerator(player.uniqueId)

    fun getCurrentIndex(player: UUID) = getGenerator(player)?.currentIndex ?: 0

    suspend fun processRun(player: UUID): Int? {
        val generator = generators.find { it.associatedPlayer == player } ?: return null
        val highscore = parkourService.getStats(player).highscore

        parkourService.saveRun(
            ParkourRun(
                this,
                player,
                generator.currentIndex,
                System.currentTimeMillis() - generator.startTime
            )
        )


        if (highscore < generator.currentIndex) {
            return generator.currentIndex
        }

        return null
    }

    suspend fun exit(player: UUID) {
        val generator = generators.find { it.associatedPlayer == player } ?: return

        generator.stop()
        generators.remove(generator)
        waitPlease.remove(player)
    }

    fun preExit(playerUuid: UUID) {
        players.remove(playerUuid)
        waitPlease.add(playerUuid)

        val player = Bukkit.getPlayer(playerUuid) ?: return
        player.teleportAsync(respawnLocation)
    }

    companion object {
        fun all() = parkourService.getParkours()
    }
}
