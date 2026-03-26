package dev.slne.surf.parkour.microservice.table

import dev.slne.surf.database.columns.nativeUuid
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object ParkourRunsTable : LongIdTable("parkour_runs") {
    val parkourUuid = nativeUuid("parkour_uuid")
    val playerUuid = nativeUuid("player_uuid").index()

    val runJumps = integer("run_jumps").default(0)
    val runTime = long("run_time").default(0L)
}