package dev.slne.surf.parkour.fallback.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object ParkourAreasTable : IntIdTable("parkour_areas") {
    val parkourId = reference("parkour_id", ParkourTable.id, ReferenceOption.CASCADE)
    val world = uuid("world")
    val firstX = double("first_x")
    val firstY = double("first_y")
    val firstZ = double("first_z")
    val secondX = double("second_x")
    val secondY = double("second_y")
    val secondZ = double("second_z")
}