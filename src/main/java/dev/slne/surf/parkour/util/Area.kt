package dev.slne.surf.parkour.util

import com.google.gson.Gson
import org.bukkit.util.Vector

data class Area (
    var max: Vector,
    var min: Vector
) {
    override fun toString(): String {
        return Gson().toJson(this)
    }

    companion object {
        fun fromString(string: String): Area {
            return Gson().fromJson(string, Area::class.java)
        }
    }
}
