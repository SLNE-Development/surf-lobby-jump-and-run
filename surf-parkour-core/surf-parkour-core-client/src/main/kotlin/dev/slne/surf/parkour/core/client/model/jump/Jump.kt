package dev.slne.surf.parkour.core.client.model.jump

import dev.slne.surf.parkour.core.client.model.geometry.ParkourRegion
import dev.slne.surf.parkour.core.client.model.geometry.ParkourVector
import dev.slne.surf.parkour.core.client.model.geometry.blockColumn
import dev.slne.surf.parkour.core.client.model.geometry.packColumn
import it.unimi.dsi.fastutil.longs.LongOpenHashSet
import it.unimi.dsi.fastutil.longs.LongSet
import it.unimi.dsi.fastutil.longs.LongSets
import kotlin.math.cos
import kotlin.math.sin

/**
 * One jump of a parkour, described relative to the block it starts from.
 *
 * @property forward how many blocks the jump leads away from the player's facing direction
 * @property lateral how many blocks the jump leads to the side
 * @property vertical how many blocks the jump leads up or down
 */
data class Jump(
    val forward: Int,
    val lateral: Int,
    val vertical: Int
) {
    companion object {
        private const val FALLBACK_FORWARD_DISTANCE = 4.0

        /**
         * The sideways offsets tried once every straight jump ahead is taken.
         */
        private val LAST_RESORT_LATERAL_OFFSETS = intArrayOf(-2, 2, -1, 1)
    }

    /**
     * Returns where this jump lands when taken from [previous] while looking at [yaw].
     *
     * The result stays inside [area] and avoids [current] as well as every block in
     * [otherPlayersBlocks], falling back to increasingly simple jumps while no free spot is found.
     */
    fun generate(
        previous: ParkourVector,
        current: ParkourVector,
        yaw: Float,
        area: ParkourRegion,
        otherPlayersBlocks: List<ParkourVector> = emptyList()
    ): ParkourVector = generate(previous, current, yaw, area, packColumns(otherPlayersBlocks))

    /**
     * Returns where this jump lands when taken from [previous] while looking at [yaw], avoiding
     * every column in [occupiedColumns] as [packColumn] writes them.
     */
    fun generate(
        previous: ParkourVector,
        current: ParkourVector,
        yaw: Float,
        area: ParkourRegion,
        occupiedColumns: LongSet
    ): ParkourVector {
        val yawRad = Math.toRadians(yaw.toDouble())
        val forwardVec = ParkourVector(-sin(yawRad), 0.0, cos(yawRad)).normalize()
        val lateralVec = ParkourVector(forwardVec.z, 0.0, -forwardVec.x)

        val target = area.clamp(
            (previous +
                    forwardVec * forward.coerceIn(2..4).toDouble() +
                    lateralVec * lateral.coerceIn(-2..2).toDouble())
                .addY(vertical.coerceIn(-1..1).toDouble())
        )

        if (!hasCollision(target, previous, current, occupiedColumns)) {
            return target
        }

        for (fallbackDistance in 3..6) {
            val fallback =
                area.clamp(previous + forwardVec * fallbackDistance.toDouble())

            if (!hasCollision(fallback, previous, current, occupiedColumns)) {
                return fallback
            }
        }

        for (lateralOffset in LAST_RESORT_LATERAL_OFFSETS) {
            val lastResort = area.clamp(
                previous + forwardVec * FALLBACK_FORWARD_DISTANCE +
                        lateralVec * lateralOffset.toDouble()
            )

            if (!hasCollision(lastResort, previous, current, occupiedColumns)) {
                return lastResort
            }
        }

        return area.clamp(previous + forwardVec * FALLBACK_FORWARD_DISTANCE)
    }

    private fun hasCollision(
        target: ParkourVector,
        previous: ParkourVector,
        current: ParkourVector,
        occupiedColumns: LongSet
    ): Boolean {
        if (previous.distance(target) < 2.0) {
            return true
        }

        val targetX = target.blockX
        val targetZ = target.blockZ

        return (targetX == previous.blockX && targetZ == previous.blockZ) ||
                (targetX == current.blockX && targetZ == current.blockZ) ||
                occupiedColumns.contains(packColumn(targetX, targetZ))
    }
}

/**
 * The columns [blocks] occupy, so a collision check costs one lookup instead of a full scan.
 */
private fun packColumns(blocks: List<ParkourVector>): LongSet {
    if (blocks.isEmpty()) {
        return LongSets.emptySet()
    }

    val columns = LongOpenHashSet(blocks.size)
    for (block in blocks) {
        columns.add(block.blockColumn)
    }

    return columns
}
