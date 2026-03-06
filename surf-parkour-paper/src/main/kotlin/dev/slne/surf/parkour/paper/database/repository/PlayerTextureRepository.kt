package dev.slne.surf.parkour.paper.database.repository

import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.insert
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.selectAll
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.parkour.api.data.PlayerTextures
import dev.slne.surf.parkour.paper.database.table.ParkourPlayerTexturesTable
import dev.slne.surf.parkour.paper.plugin
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import it.unimi.dsi.fastutil.objects.ObjectSet
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toSet

val playerTextureRepository = PlayerTextureRepository()

const val MAX_PLAYER_TEXTURES = 50_000

class PlayerTextureRepository {
    suspend fun fetchTextures(): ObjectSet<PlayerTextures> = suspendTransaction {
        ParkourPlayerTexturesTable.selectAll().map {
            PlayerTextures(
                playerUuid = it[ParkourPlayerTexturesTable.playerUuid],
                playerName = it[ParkourPlayerTexturesTable.playerName],
                texture = it[ParkourPlayerTexturesTable.texture]
            )
        }.toSet().toObjectSet()
    }.also {
        if (it.size > MAX_PLAYER_TEXTURES) {
            plugin.logger.severe("There are more than $MAX_PLAYER_TEXTURES player textures in the database. This may cause performance issues.")
        }
    }

    suspend fun saveTexture(playerTextures: PlayerTextures) = suspendTransaction {
        ParkourPlayerTexturesTable.insert {
            it[playerUuid] = playerTextures.playerUuid
            it[playerName] = playerTextures.playerName
            it[texture] = playerTextures.texture
        }
    }
}