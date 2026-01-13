package dev.slne.surf.parkour.paper.database

import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import dev.slne.surf.parkour.paper.database.type.boundingBox
import dev.slne.surf.parkour.paper.database.type.location
import dev.slne.surf.parkour.paper.database.type.world

object ParkourTable : LongIdTable("parkour_parkours") {
    val parkourUuid = uuid("uuid").uniqueIndex()
    val serverUuid = uuid("server_uuid")
    val identifier = varchar("identifier", 100).uniqueIndex()
    val displayName = varchar("display_name", 100)
    val world = world("world")
    val boundingBox = boundingBox("bounding_box")
    val respawnLocation = location("respawn_location")
}