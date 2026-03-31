package dev.slne.surf.parkour.core.common.rabbit.packet.request

import dev.slne.surf.parkour.core.common.rabbit.packet.response.ManyParkourStatsResponsePacket
import dev.slne.surf.rabbitmq.api.packet.RabbitRequestPacket
import kotlinx.serialization.Serializable

@Serializable
class LoadAllParkourStatsRequestPacket : RabbitRequestPacket<ManyParkourStatsResponsePacket>()
