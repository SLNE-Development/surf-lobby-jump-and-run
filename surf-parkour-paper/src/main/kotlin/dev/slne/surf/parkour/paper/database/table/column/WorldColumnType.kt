package dev.slne.surf.parkour.paper.database.table.column

import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.Column
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.ColumnType
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.Table
import org.bukkit.Bukkit
import org.bukkit.World
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

