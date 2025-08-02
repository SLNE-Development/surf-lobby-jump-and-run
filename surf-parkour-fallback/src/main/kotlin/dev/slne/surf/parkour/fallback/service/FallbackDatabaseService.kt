package dev.slne.surf.parkour.fallback.service

import dev.slne.surf.database.database.DatabaseProvider
import dev.slne.surf.parkour.core.service.DatabaseService

class FallbackDatabaseService : DatabaseService {
    override fun connect() {
        DatabaseProvider
    }

    override fun createTables() {
        TODO("Not yet implemented")
    }

    override fun disconnect() {
        TODO("Not yet implemented")
    }
}