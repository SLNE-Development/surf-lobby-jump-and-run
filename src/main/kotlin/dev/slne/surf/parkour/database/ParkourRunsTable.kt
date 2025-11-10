package dev.slne.surf.parkour.database

import org.jetbrains.exposed.dao.id.LongIdTable

object ParkourRunsTable : LongIdTable("parkour_runs") {
    val parkourUuid = uuid("parkour_uuid").references(ParkourTable.parkourUuid)
    val playerUuid = uuid("player_uuid")

    val parkour_jumps = integer("run_jumps").default(0)
    val parkour_time = long("run_time").default(0L)
}