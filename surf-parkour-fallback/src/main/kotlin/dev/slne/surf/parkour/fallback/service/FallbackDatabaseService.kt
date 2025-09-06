package dev.slne.surf.parkour.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.database.DatabaseManager
import dev.slne.surf.database.database.DatabaseProvider
import dev.slne.surf.parkour.core.service.DatabaseService
import dev.slne.surf.parkour.core.service.parkourPlayerService
import dev.slne.surf.parkour.core.service.parkourService
import dev.slne.surf.parkour.core.service.parkourStatisticsService
import net.kyori.adventure.util.Services
import java.nio.file.Path

@AutoService(DatabaseService::class)
class FallbackDatabaseService : DatabaseService, Services.Fallback {
    private lateinit var databaseProvider: DatabaseProvider
    override fun connect(path: Path) {
        databaseProvider = DatabaseManager(path, path).databaseProvider
        databaseProvider.connect()
    }

    override fun createTables() {
        parkourStatisticsService.createTable()
        parkourService.createTable()
        parkourPlayerService.createTable()
    }

    override fun disconnect() {
        databaseProvider.disconnect()
    }
}