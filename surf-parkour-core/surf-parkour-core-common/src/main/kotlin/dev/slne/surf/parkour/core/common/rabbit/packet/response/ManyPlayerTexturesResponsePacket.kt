package dev.slne.surf.parkour.core.common.rabbit.packet.response

import dev.slne.surf.parkour.api.data.PlayerTextures
import dev.slne.surf.rabbitmq.api.packet.RabbitResponsePacket
import kotlinx.serialization.Serializable

@Serializable
data class ManyPlayerTexturesResponsePacket(
    val playerTextures: List<PlayerTextures>
) : RabbitResponsePacket()
