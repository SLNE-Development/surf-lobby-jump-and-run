package dev.slne.surf.parkour.paper.database

import org.jetbrains.exposed.dao.id.LongIdTable

object ParkourRunsTable : LongIdTable("parkour_runs") {
    val parkourUuid = uuid("parkour_uuid").references(ParkourTable.parkourUuid)
    val playerUuid = uuid("player_uuid")

    val runJumps = integer("run_jumps").default(0)
    val runTime = long("run_time").default(0L)
}