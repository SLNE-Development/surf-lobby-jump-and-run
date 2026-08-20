package dev.slne.surf.parkour.core.client.model.geometry

import java.lang.Math.toDegrees
import kotlin.math.atan2
import kotlin.math.sqrt

/**
 * Returns the yaw and pitch someone standing at [from] has to look at to face [to].
 */
fun calcRotation(from: ParkourVector, to: ParkourVector): Pair<Float, Float> {
    val dir = to - from
    val dx = dir.x
    val dy = dir.y
    val dz = dir.z

    val yaw = toDegrees(atan2(dz, dx)) - 90
    val pitch = -toDegrees(atan2(dy, sqrt(dx * dx + dz * dz)))

    return Pair(yaw.toFloat(), pitch.toFloat())
}
