package dev.slne.surf.parkour.paper.database.type

import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.Column
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.ColumnType
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.Table
import org.bukkit.util.BoundingBox
import java.sql.Clob

class BoundingBoxColumnType : ColumnType<BoundingBox>() {
    override fun sqlType(): String = "VARCHAR(255)"

    override fun notNullValueToDB(value: BoundingBox) =
        "${value.minX};${value.minY};${value.minZ};${value.maxX};${value.maxY};${value.maxZ}"

    override fun valueFromDB(value: Any): BoundingBox {
        val string = when (value) {
            is Clob -> value.characterStream.readText()
            is String -> value
            else -> value.toString()
        }

        val parts = string.split(";")
        if (parts.size < 6) throw IllegalArgumentException("Invalid BoundingBox format: $string")

        return BoundingBox(
            parts[0].toDouble(),
            parts[1].toDouble(),
            parts[2].toDouble(),
            parts[3].toDouble(),
            parts[4].toDouble(),
            parts[5].toDouble()
        )
    }
}

fun Table.boundingBox(name: String): Column<BoundingBox> =
    registerColumn(name, BoundingBoxColumnType())

