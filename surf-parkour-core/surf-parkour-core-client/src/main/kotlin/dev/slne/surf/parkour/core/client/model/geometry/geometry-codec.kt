package dev.slne.surf.parkour.core.client.model.geometry

/**
 * Writes [region] as `minX,minY,minZ;maxX,maxY,maxZ`.
 */
fun serializeRegion(region: ParkourRegion): String {
    val min = region.min
    val max = region.max
    return "${min.x},${min.y},${min.z};${max.x},${max.y},${max.z}"
}

/**
 * Reads a region written by [serializeRegion].
 *
 * @throws IllegalStateException if [data] does not hold two corners of three coordinates each
 */
fun deserializeRegion(data: String): ParkourRegion {
    val parts = data.split(";")
    if (parts.size != 2) error("Invalid bounding box format")

    val minParts = parts[0].split(",").map { it.toDouble() }
    val maxParts = parts[1].split(",").map { it.toDouble() }

    if (minParts.size != 3 || maxParts.size != 3) error("Invalid bounding box format")

    val min = ParkourVector(minParts[0], minParts[1], minParts[2])
    val max = ParkourVector(maxParts[0], maxParts[1], maxParts[2])

    return ParkourRegion.of(min, max)
}

/**
 * Writes [location] as `world,x,y,z,yaw,pitch`.
 */
fun serializeLocation(location: ParkourLocation): String {
    return "${location.world},${location.x},${location.y},${location.z},${location.yaw},${location.pitch}"
}

/**
 * Reads a location written by [serializeLocation].
 *
 * @throws IllegalStateException if [data] does not hold a world and five coordinates
 */
fun deserializeLocation(data: String): ParkourLocation {
    val parts = data.split(",")
    if (parts.size != 6) error("Invalid location format")

    return ParkourLocation(
        world = parts[0],
        x = parts[1].toDouble(),
        y = parts[2].toDouble(),
        z = parts[3].toDouble(),
        yaw = parts[4].toFloat(),
        pitch = parts[5].toFloat()
    )
}
