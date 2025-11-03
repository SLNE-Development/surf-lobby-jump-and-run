package dev.slne.surf.parkour.core.service

import dev.slne.surf.surfapi.core.api.util.requiredService
import java.nio.file.Path

interface DatabaseService {
    fun connect(path: Path)
    fun createTables()
    fun disconnect()

    companion object {
        val INSTANCE = requiredService<DatabaseService>()
    }
}

val databaseService get() = DatabaseService.INSTANCE