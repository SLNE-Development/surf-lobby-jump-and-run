package dev.slne.surf.parkour.core.client.service

import dev.slne.surf.parkour.core.client.model.geometry.ParkourVector
import java.util.*
import kotlin.time.Duration.Companion.seconds

/**
 * Turns the movement of a player into the failures and successes of the parkour they run.
 *
 * Platforms feed every movement that crossed a block boundary into [processMove]; where a player
 * stands is all that is needed to tell a landed jump from a fall.
 */
object ParkourMoveService {

    /**
     * How long after a parkour starts a player cannot fall out of it, so that the teleport onto the
     * first block is never mistaken for a fall.
     */
    private val startGrace = 1.seconds.inWholeMilliseconds

    /**
     * Reacts to the player identified by [playerUuid] having moved to the given position.
     */
    fun processMove(playerUuid: UUID, x: Double, y: Double, z: Double) {
        val parkour = ParkourService.getParkourByPlayer(playerUuid) ?: return
        val generator = parkour.getGenerator(playerUuid) ?: return
        val blockLocations = generator.blockLocations ?: return

        if (hasFallenBelow(blockLocations, y) &&
            System.currentTimeMillis() - generator.startTime >= startGrace
        ) {
            ParkourService.triggerFailure(playerUuid)
            return
        }

        if (isOnTargetBlock(x, y - 1.0, z, blockLocations.second)) {
            ParkourService.triggerSuccess(playerUuid)
        }
    }

    /**
     * Returns whether every block of a running parkour sits above [y], which means the player who
     * runs it has dropped below all of them.
     */
    fun hasFallenBelow(
        blockLocations: Triple<ParkourVector, ParkourVector, ParkourVector>,
        y: Double
    ) = blockLocations.first.y > y &&
            blockLocations.second.y > y &&
            blockLocations.third.y > y

    /**
     * Returns whether a player standing at the given position is on top of [target].
     */
    fun isOnTargetBlock(x: Double, y: Double, z: Double, target: ParkourVector): Boolean {
        val blockCenterX = target.blockX + 0.5
        val blockCenterZ = target.blockZ + 0.5

        return y.toInt() == target.blockY
                && x >= blockCenterX - 1.0 && x <= blockCenterX + 1.0
                && z >= blockCenterZ - 1.0 && z <= blockCenterZ + 1.0
    }
}
