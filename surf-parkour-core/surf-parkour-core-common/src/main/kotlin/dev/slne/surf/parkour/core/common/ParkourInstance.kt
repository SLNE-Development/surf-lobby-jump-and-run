package dev.slne.surf.parkour.core.common

import dev.slne.surf.rabbitmq.api.RabbitMQApi
import dev.slne.surf.surfapi.core.api.util.requiredService

private val instance = requiredService<ParkourInstance>()

interface ParkourInstance {
    val rabbitApi: RabbitMQApi

    companion object : ParkourInstance by instance {
        val INSTANCE get() = instance
    }
}