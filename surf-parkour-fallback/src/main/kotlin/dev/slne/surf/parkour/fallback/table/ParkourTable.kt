package dev.slne.surf.parkour.fallback.table

import org.jetbrains.exposed.dao.id.IntIdTable

object ParkourTable : IntIdTable("parkour_parkours") {
    val uuid = uuid("uuid")
    val name = varchar("name", 255)
    val serverUuid = uuid("server_uuid")
}