package dev.slne.surf.parkour.microservice.repository

import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.select
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.upsert
import dev.slne.surf.parkour.api.data.PlayerTextures
import dev.slne.surf.parkour.microservice.table.ParkourPlayerTexturesTable
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList

object PlayerTextureRepository {
    suspend fun fetchTextures(): List<PlayerTextures> = suspendTransaction {
        ParkourPlayerTexturesTable.select(
            ParkourPlayerTexturesTable.playerUuid,
            ParkourPlayerTexturesTable.playerName
        ).map {
            PlayerTextures(
                playerUuid = it[ParkourPlayerTexturesTable.playerUuid],
                playerName = it[ParkourPlayerTexturesTable.playerName],
                texture = "ewogICJ0aW1lc3RhbXAiIDogMTc3NTA2MjI5NTk2NiwKICAicHJvZmlsZUlkIiA6ICI4NmRhMzAzYjBmOTA0M2JhYWU3ZmJkMjNjZGJmYjBiYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJKaW56YXJ1IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2Y4NWVkMzg2ZDZmOWFmYmI1MjJkMWQ1ZGJmNDI3YTA5YWFhZTM5YjUxYmI5MWY0MWI2NjQ4NDZjMWRlYjExYzUiCiAgICB9LAogICAgIkNBUEUiIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzI4ZGU0YTgxNjg4YWQxOGI0OWU3MzVhMjczZTA4NmMxOGYxZTM5NjY5NTYxMjNjY2I1NzQwMzRjMDZmNWQzMzYiCiAgICB9CiAgfQp9"
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