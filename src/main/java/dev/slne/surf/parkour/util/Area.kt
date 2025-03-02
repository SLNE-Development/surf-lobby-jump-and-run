package dev.slne.surf.parkour.util

import org.bukkit.util.Vector

data class Area (
    var max: Vector,
    var min: Vector
) {
    override fun toString(): String {
        return "Area(max=$max, min=$min)"
    }

    companion object {
        fun fromString(string: String): Area {
            val split = string.split(" ")
            val max = Vector(split[0].toDouble(), split[1].toDouble(), split[2].toDouble())
            val min = Vector(split[3].toDouble(), split[4].toDouble(), split[5].toDouble())
            return Area(max, min)
        }
    }
}
