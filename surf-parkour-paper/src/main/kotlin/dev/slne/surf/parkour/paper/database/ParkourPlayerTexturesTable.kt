package dev.slne.surf.parkour.paper.database

import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object ParkourPlayerTexturesTable : LongIdTable("parkour_textures") {
    val playerUuid = uuid("player_uuid").uniqueIndex()
    val texture = text("texture")
}