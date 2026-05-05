package dev.slne.surf.parkour.paper.database.table

import dev.slne.surf.database.columns.nativeUuid
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object ParkourPlayerTexturesTable : LongIdTable("parkour_textures") {
    val playerUuid = nativeUuid("player_uuid").uniqueIndex()
    val playerName = varchar("player_name", 16)
    val texture = text("texture")
}