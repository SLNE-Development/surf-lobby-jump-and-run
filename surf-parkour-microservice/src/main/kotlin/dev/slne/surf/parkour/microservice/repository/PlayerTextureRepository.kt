package dev.slne.surf.parkour.microservice.repository

import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.selectAll
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.upsert
import dev.slne.surf.parkour.api.data.PlayerTextures
import dev.slne.surf.parkour.microservice.table.ParkourPlayerTexturesTable
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList

object PlayerTextureRepository {
    suspend fun fetchTextures(): List<PlayerTextures> = suspendTransaction {
        ParkourPlayerTexturesTable.selectAll().map {
            PlayerTextures(
                playerUuid = it[ParkourPlayerTexturesTable.playerUuid],
                playerName = it[ParkourPlayerTexturesTable.playerName],
                texture = it[ParkourPlayerTexturesTable.texture]
            )
        }.toList()
    }

    suspend fun saveTexture(playerTextures: PlayerTextures) = suspendTransaction {
        ParkourPlayerTexturesTable.upsert {
            it[playerUuid] = playerTextures.playerUuid
            it[playerName] = playerTextures.playerName
            it[texture] = playerTextures.texture
        }
    }
}