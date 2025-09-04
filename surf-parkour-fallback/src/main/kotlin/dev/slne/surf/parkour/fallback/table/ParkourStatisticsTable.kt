package dev.slne.surf.parkour.fallback.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object ParkourStatisticsTable : IntIdTable("parkour_statistics") {
    val parkourId = reference("parkour_id", ParkourTable.id, ReferenceOption.CASCADE)
    val userUuid = uuid("user_uuid")
    val time = long("time").default(0)
    val jumps = integer("jumps").default(0)
}