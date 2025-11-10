package dev.slne.surf.parkour.database

import org.jetbrains.exposed.dao.id.LongIdTable

object ParkourEntriesTable : LongIdTable("parkour_entries") {
    val parkourUuid = uuid("parkour_uuid").references(ParkourTable.parkourUuid)
    val playerUuid = uuid("player_uuid")

    val parkour_jumps = integer("parkour_jumps").default(0)
    val parkour_time = long("parkour_time").default(0L)
}