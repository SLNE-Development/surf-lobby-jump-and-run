package dev.slne.surf.parkour.core.client.model.parkour

import dev.slne.surf.api.core.generated.BlockTypeKeys
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.core.util.mutableObjectSetOf
import dev.slne.surf.api.core.util.object2ObjectMapOf
import dev.slne.surf.api.core.util.objectListOf
import dev.slne.surf.parkour.api.data.ParkourRun
import dev.slne.surf.parkour.core.client.model.geometry.ParkourLocation
import dev.slne.surf.parkour.core.client.model.geometry.ParkourRegion
import dev.slne.surf.parkour.core.client.platform.ParkourPlatform
import dev.slne.surf.parkour.core.client.service.ParkourRunsService
import dev.slne.surf.parkour.core.client.service.ParkourService
import net.kyori.adventure.key.Key
import java.util.*
import java.util.concurrent.ConcurrentHashMap

data class Parkour(
    val uuid: UUID,
    val identifier: String,
    val displayName: String,
    val boundingBox: ParkourRegion,
    val world: String,
    val respawnLocation: ParkourLocation
) {
    val generators: ConcurrentHashMap.KeySetView<ParkourGenerator, Boolean> =
        ConcurrentHashMap.newKeySet()
    val players = mutableObjectSetOf<UUID>()
    val waitPlease = mutableObjectSetOf<UUID>()

    private val concretes = objectListOf(
        BlockTypeKeys.RED_CONCRETE,
        BlockTypeKeys.BLUE_CONCRETE,
        BlockTypeKeys.GREEN_CONCRETE,
        BlockTypeKeys.YELLOW_CONCRETE,
        BlockTypeKeys.ORANGE_CONCRETE,
        BlockTypeKeys.PURPLE_CONCRETE,
        BlockTypeKeys.CYAN_CONCRETE,
        BlockTypeKeys.MAGENTA_CONCRETE
    )

    private val playerMaterials = object2ObjectMapOf(
        UUID.fromString("1c779cb1-3860-4e23-9cac-7f160b2acc61") to BlockTypeKeys.RED_CONCRETE, // TheBjoRedCraft
        UUID.fromString("3094f2ed-fe51-46fd-a1dc-d76e846548e5") to BlockTypeKeys.FLOWERING_AZALEA_LEAVES // Floweryalina
    )

    fun getBlockMaterial(player: UUID): Key = playerMaterials[player] ?: concretes.random()

    suspend fun start(player: UUID): Boolean {
        val generator = ParkourGenerator(player, this@Parkour, getBlockMaterial(player))

        if (ParkourService.isInParkour(player)) {
            ParkourPlatform.audience(player)?.let {
                it.sendText {
                    appendErrorPrefix()
                    error("Du bist bereits in einem Parkour!")
                }
            }
            return false
        }

        if (waitPlease.contains(player)) {
            ParkourPlatform.audience(player)?.let {
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

    fun getCurrentIndex(player: UUID) = getGenerator(player)?.currentIndex ?: 0

    fun processRun(player: UUID): Int? {
        val generator = generators.find { it.associatedPlayer == player } ?: return null
        val highscore = ParkourRunsService.getStats(player).highscore

        ParkourPlatform.launch {
            ParkourRunsService.saveRun(
                ParkourRun(
                    this@Parkour.uuid,
                    player,
                    generator.currentIndex,
                    System.currentTimeMillis() - generator.startTime
                )
            )
        }

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

        ParkourPlatform.teleport(playerUuid, respawnLocation)
    }
}
