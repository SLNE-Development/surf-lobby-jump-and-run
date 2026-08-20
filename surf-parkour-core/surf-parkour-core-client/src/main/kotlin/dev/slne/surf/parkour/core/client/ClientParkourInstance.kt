package dev.slne.surf.parkour.core.client

import dev.slne.surf.parkour.core.common.ParkourInstance
import dev.slne.surf.rabbitmq.api.ClientRabbitMQApi
import java.nio.file.Path

/**
 * The parkour instance as it exists on a game server, regardless of the platform that server runs.
 */
interface ClientParkourInstance : ParkourInstance {
    val clientLoader: ClientLoader

    /**
     * The directory this plugin stores its own files in.
     */
    val dataPath: Path

    override val rabbitApi: ClientRabbitMQApi get() = clientLoader.rabbitApi

    companion object : ClientParkourInstance by ParkourInstance.INSTANCE as ClientParkourInstance {
        val INSTANCE get() = ParkourInstance.INSTANCE
    }
}
