package dev.slne.surf.parkour.core.paper

import dev.slne.surf.rabbitmq.api.ClientRabbitMQApi
import java.nio.file.Path

class PaperLoader(
    dataPath: Path
) {
    val rabbitApi = ClientRabbitMQApi.create("surf-parkour", dataPath)

    suspend fun onLoad() {
        // Rabbit
        rabbitApi.freezeAndConnect()
    }

    suspend fun onEnable() {
    }

    suspend fun onDisable() {
        rabbitApi.disconnect()
    }
}