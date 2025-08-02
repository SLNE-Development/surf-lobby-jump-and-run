package dev.slne.surf.parkour.core.service

import dev.slne.surf.surfapi.core.api.util.requiredService

interface DatabaseService {
    fun connect()
    fun createTables()
    fun disconnect()

    companion object {
        val INSTANCE = requiredService<DatabaseService>()
    }
}

val databaseService get() = DatabaseService.INSTANCE