package dev.slne.surf.parkour.core.common.rabbit.packet.request

import dev.slne.surf.parkour.api.data.PlayerTextures
import dev.slne.surf.rabbitmq.api.packet.RabbitRequestPacket
import dev.slne.surf.rabbitmq.api.packet.standard.response.primitive.PrimitiveResponse
import kotlinx.serialization.Serializable

@Serializable
data class SavePlayerTexturesRequestPacket(
    val playerTextures: PlayerTextures
) : RabbitRequestPacket<PrimitiveResponse.BooleanResponsePacket>()
