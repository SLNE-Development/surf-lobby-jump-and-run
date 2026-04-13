package dev.slne.surf.parkour.core.common.rabbit.packet.request

import dev.slne.surf.api.core.serializer.java.uuid.SerializableUUID
import dev.slne.surf.parkour.core.common.rabbit.packet.response.SingleParkourStatResponsePacket
import dev.slne.surf.rabbitmq.api.packet.RabbitRequestPacket
import kotlinx.serialization.Serializable

@Serializable
data class LoadPlayerStatsRequestPacket(
    val playerUuid: SerializableUUID
) : RabbitRequestPacket<SingleParkourStatResponsePacket>()
