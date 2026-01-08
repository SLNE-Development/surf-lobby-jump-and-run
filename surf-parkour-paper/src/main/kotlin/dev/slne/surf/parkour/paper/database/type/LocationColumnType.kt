package dev.slne.surf.parkour.paper.database.type

import org.bukkit.Bukkit
import org.bukkit.Location
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import java.sql.Clob

class LocationColumnType : ColumnType<Location>() {
    override fun sqlType(): String = "VARCHAR(255)"
    override fun notNullValueToDB(value: Location): Any {
        val world = value.world?.name ?: "null"
        return "$world;${value.x};${value.y};${value.z};${value.yaw};${value.pitch}"
    }

    override fun valueFromDB(value: Any): Location {
        val string = when (value) {
            is Clob -> value.characterStream.readText()
            is String -> value
            else -> value.toString()
        }

        val parts = string.split(";")
        val world = Bukkit.getWorld(parts[0]) ?: return Location(
            null,
            parts[1].toDouble(),
            parts[2].toDouble(),
            parts[3].toDouble()
        )
        return Location(
            world,
            parts[1].toDouble(),
            parts[2].toDouble(),
            parts[3].toDouble(),
            parts.getOrNull(4)?.toFloat() ?: 0f,
            parts.getOrNull(5)?.toFloat() ?: 0f
        )
    }
}

fun Table.location(name: String): Column<Location> =
    registerColumn(name, LocationColumnType())

