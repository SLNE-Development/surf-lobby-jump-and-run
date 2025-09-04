package dev.slne.surf.parkour.fallback.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object ParkourStatisticsTable : IntIdTable("parkour_statistics") {
    val parkourId = reference("parkour_id", ParkourTable.id, ReferenceOption.CASCADE)
    val userUuid = uuid("user_uuid")
    val tries = integer("tries").default(0)
    val failures = integer("failures").default(0)
    val bestTry = integer("best_try").default(0)
    val overallJumps = integer("overall_jumps").default(0)
}