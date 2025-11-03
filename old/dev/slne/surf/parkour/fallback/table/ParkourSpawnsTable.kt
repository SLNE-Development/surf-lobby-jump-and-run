package dev.slne.surf.parkour.fallback.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object ParkourSpawnsTable : IntIdTable("parkour_spawns") {
    val parkourId = reference("parkour_id", ParkourTable.id, ReferenceOption.CASCADE)
    val world = uuid("world")
    val x = double("x")
    val y = double("y")
    val z = double("z")
    val yaw = float("yaw")
    val pitch = float("pitch")
}