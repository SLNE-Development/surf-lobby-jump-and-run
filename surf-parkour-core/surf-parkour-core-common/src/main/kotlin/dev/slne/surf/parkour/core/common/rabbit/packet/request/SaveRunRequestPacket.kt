package dev.slne.surf.parkour.core.common.rabbit.packet.request

import dev.slne.surf.parkour.api.data.ParkourRun
import dev.slne.surf.rabbitmq.api.packet.RabbitRequestPacket
import dev.slne.surf.rabbitmq.api.packet.standard.response.primitive.PrimitiveResponse
import kotlinx.serialization.Serializable

@Serializable
data class SaveRunRequestPacket(
    val run: ParkourRun
) : RabbitRequestPacket<PrimitiveResponse.BooleanResponsePacket>()
