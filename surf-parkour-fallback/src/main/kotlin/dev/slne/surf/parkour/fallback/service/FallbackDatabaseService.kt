package dev.slne.surf.parkour.fallback.service

import dev.slne.surf.database.DatabaseManager
import dev.slne.surf.database.database.DatabaseProvider
import dev.slne.surf.parkour.core.service.DatabaseService
import dev.slne.surf.parkour.core.service.parkourStatisticsService
import java.nio.file.Path

class FallbackDatabaseService : DatabaseService {
    private lateinit var databaseProvider: DatabaseProvider
    override fun connect(path: Path) {
        databaseProvider = DatabaseManager(path, path).databaseProvider
        databaseProvider.connect()
    }

    override fun createTables() {
        parkourStatisticsService.createTable()
    }

    override fun disconnect() {
        databaseProvider.disconnect()
    }
}