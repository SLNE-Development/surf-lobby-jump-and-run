package dev.slne.surf.parkour.microservice

import com.google.auto.service.AutoService
import dev.slne.surf.database.DatabaseApi
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.SchemaUtils
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.microservice.api.microservice.Microservice
import dev.slne.surf.parkour.microservice.rabbit.handler.ParkourRunsHandler
import dev.slne.surf.parkour.microservice.rabbit.handler.ParkourTextureHandler
import dev.slne.surf.parkour.microservice.table.ParkourPlayerTexturesTable
import dev.slne.surf.parkour.microservice.table.ParkourRunsTable
import dev.slne.surf.rabbitmq.api.ServerRabbitMQApi
import kotlin.io.path.Path

@AutoService(Microservice::class)
class ParkourMicroservice : Microservice() {
    override val dataPath = Path("config")
    private val databaseApi = DatabaseApi.create(dataPath)
    private val rabbitApi = ServerRabbitMQApi.create("surf-parkour", dataPath)

    override suspend fun onBootstrap(args: List<String>) {
        suspendTransaction {
            SchemaUtils.create(
                ParkourPlayerTexturesTable,
                ParkourRunsTable
            )
        }

        rabbitApi.registerRequestHandler(ParkourRunsHandler)
        rabbitApi.registerRequestHandler(ParkourTextureHandler)
        rabbitApi.freezeAndConnect()
    }

    override suspend fun onDisable() {
        rabbitApi.disconnect()
        databaseApi.shutdown()
    }
}