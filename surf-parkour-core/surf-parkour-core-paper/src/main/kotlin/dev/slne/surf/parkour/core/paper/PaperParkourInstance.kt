package dev.slne.surf.parkour.core.paper

import dev.slne.surf.parkour.core.common.ParkourInstance
import dev.slne.surf.rabbitmq.api.ClientRabbitMQApi

interface PaperParkourInstance : ParkourInstance {
    val paperLoader: PaperLoader

    override val rabbitApi: ClientRabbitMQApi get() = paperLoader.rabbitApi

    companion object : PaperParkourInstance by ParkourInstance.INSTANCE as PaperParkourInstance {
        val INSTANCE get() = ParkourInstance.INSTANCE
    }
}