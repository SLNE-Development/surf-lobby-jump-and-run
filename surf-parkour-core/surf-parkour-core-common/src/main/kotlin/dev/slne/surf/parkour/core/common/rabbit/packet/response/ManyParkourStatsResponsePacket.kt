package dev.slne.surf.parkour.core.common.rabbit.packet.response

import dev.slne.surf.parkour.api.data.ParkourStats
import dev.slne.surf.rabbitmq.api.packet.RabbitResponsePacket
import kotlinx.serialization.Serializable

@Serializable
data class ManyParkourStatsResponsePacket(
    val stats: List<ParkourStats>
) : RabbitResponsePacket()
