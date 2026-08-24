package dev.slne.surf.parkour.core.client.model.geometry

/**
 * The column the block at [blockX]/[blockZ] stands in, packed into a single long.
 */
fun packColumn(blockX: Int, blockZ: Int): Long =
    (blockX.toLong() shl 32) or (blockZ.toLong() and 0xFFFF_FFFFL)

/**
 * The column the block this vector lies in occupies, as [packColumn] writes it.
 */
val ParkourVector.blockColumn: Long get() = packColumn(blockX, blockZ)
