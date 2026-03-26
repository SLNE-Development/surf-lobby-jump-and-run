package dev.slne.surf.parkour.core.common.rabbit.packet.request

import dev.slne.surf.parkour.core.common.rabbit.packet.response.SingleParkourStatResponsePacket
import dev.slne.surf.rabbitmq.api.packet.RabbitRequestPacket
import dev.slne.surf.surfapi.core.api.serializer.java.uuid.SerializableUUID
import kotlinx.serialization.Serializable

@Serializable
data class LoadPlayerStatsRequestPacket(
    val playerUuid: SerializableUUID
) : RabbitRequestPacket<SingleParkourStatResponsePacket>()
