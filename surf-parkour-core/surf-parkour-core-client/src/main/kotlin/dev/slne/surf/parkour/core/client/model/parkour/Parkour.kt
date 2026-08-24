package dev.slne.surf.parkour.core.client.model.parkour

import dev.slne.surf.api.core.generated.BlockTypeKeys
import dev.slne.surf.api.core.messages.adventure.sendText
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
import java.util.concurrent.ConcurrentMap

data class Parkour(
    val uuid: UUID,
    val identifier: String,
    val displayName: String,
    val boundingBox: ParkourRegion,
    val world: String,
    val respawnLocation: ParkourLocation
) {
    /**
     * The generator of everyone this parkour has built blocks for, keyed by player.
     */
    val generators: ConcurrentMap<UUID, ParkourGenerator> = ConcurrentHashMap()

    /**
     * Everyone currently counted as running this parkour.
     */
    val players: MutableSet<UUID> = ConcurrentHashMap.newKeySet()

    /**
     * Everyone who just left this parkour and may not re-enter until their blocks are cleared.
     */
    val waitPlease: MutableSet<UUID> = ConcurrentHashMap.newKeySet()

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
        if (ParkourService.isInParkour(player)) {
            sendAlreadyInParkour(player)
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

        val generator = ParkourGenerator(player, this@Parkour, getBlockMaterial(player))
        if (generators.putIfAbsent(player, generator) != null) {
            sendAlreadyInParkour(player)
            return false
        }

        var running = false

        try {
            generator.start()
            running = generator.isRunning()
        } finally {
            if (!running) {
                generators.remove(player, generator)
            }
        }

        if (running) {
            players.add(player)
        }

        return running
    }

    private fun sendAlreadyInParkour(player: UUID) {
        ParkourPlatform.audience(player)?.let {
            it.sendText {
                appendErrorPrefix()
                error("Du bist bereits in einem Parkour!")
            }
        }
    }

    fun getGenerator(player: UUID) = generators[player]

    fun getCurrentIndex(player: UUID) = getGenerator(player)?.currentIndex ?: 0

    fun processRun(player: UUID): Int? {
        val generator = generators[player] ?: return null
        val highscore = ParkourRunsService.getStats(player).highscore
        val jumps = generator.currentIndex

        ParkourPlatform.launch {
            ParkourRunsService.saveRun(
                ParkourRun(
                    this@Parkour.uuid,
                    player,
                    jumps,
                    System.currentTimeMillis() - generator.startTime
                )
            )
        }

        if (highscore < jumps) {
            return jumps
        }

        return null
    }

    suspend fun exit(player: UUID) {
        val generator = generators[player] ?: return

        generator.stop()
        generators.remove(player, generator)

        waitPlease.remove(player)
    }

    /**
     * Takes the player identified by [playerUuid] out of this parkour and back to its respawn.
     */
    fun preExit(playerUuid: UUID): Boolean {
        if (!players.remove(playerUuid)) {
            return false
        }

        waitPlease.add(playerUuid)

        ParkourPlatform.teleport(playerUuid, respawnLocation)

        return true
    }
}
