package dev.slne.surf.parkour.core.common.rabbit.packet.request

import dev.slne.surf.parkour.core.common.rabbit.packet.response.ManyPlayerTexturesResponsePacket
import dev.slne.surf.rabbitmq.api.packet.RabbitRequestPacket
import kotlinx.serialization.Serializable

@Serializable
object LoadAllPlayerTexturesRequestPacket : RabbitRequestPacket<ManyPlayerTexturesResponsePacket>()
