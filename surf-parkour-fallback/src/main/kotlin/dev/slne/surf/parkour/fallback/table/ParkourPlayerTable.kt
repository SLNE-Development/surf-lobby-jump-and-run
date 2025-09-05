package dev.slne.surf.parkour.fallback.table

import org.jetbrains.exposed.dao.id.IntIdTable

object ParkourPlayerTable : IntIdTable("parkour_parkours") {
    val uuid = uuid("uuid")
    val name = varchar("name", 255)
}