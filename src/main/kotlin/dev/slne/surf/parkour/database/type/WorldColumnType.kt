package dev.slne.surf.parkour.database.type

import org.bukkit.Bukkit
import org.bukkit.World
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import java.sql.Clob

class WorldColumnType : ColumnType<World>() {
    override fun sqlType(): String = "VARCHAR(128)"
    override fun notNullValueToDB(value: World) = value.name
    override fun valueFromDB(value: Any): World {
        val name = when (value) {
            is Clob -> value.characterStream.readText()
            is String -> value
            else -> value.toString()
        }

        return Bukkit.getWorld(name)
            ?: throw IllegalStateException("World '$name' cannot be found in the server. (error while reading from database)")
    }
}

fun Table.world(name: String): Column<World> =
    registerColumn(name, WorldColumnType())

