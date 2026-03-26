package dev.slne.surf.parkour.microservice.rabbit.handler

import dev.slne.surf.parkour.core.common.rabbit.packet.request.LoadAllPlayerTexturesRequestPacket
import dev.slne.surf.parkour.core.common.rabbit.packet.request.SavePlayerTexturesRequestPacket
import dev.slne.surf.parkour.core.common.rabbit.packet.response.ManyPlayerTexturesResponsePacket
import dev.slne.surf.parkour.microservice.repository.PlayerTextureRepository
import dev.slne.surf.rabbitmq.api.handler.RabbitHandler
import dev.slne.surf.rabbitmq.api.packet.standard.response.primitive.PrimitiveResponse
import kotlinx.coroutines.launch

object ParkourTextureHandler {
    @RabbitHandler
    fun handleLoadAllPlayerTexturesRequestPacket(packet: LoadAllPlayerTexturesRequestPacket) =
        packet.launch {
            packet.respond(ManyPlayerTexturesResponsePacket(PlayerTextureRepository.fetchTextures()))
        }

    @RabbitHandler
    fun handleSavePlayerTexturesRequestPacket(packet: SavePlayerTexturesRequestPacket) =
        packet.launch {
            packet.respond(
                PrimitiveResponse.BooleanResponsePacket(
                    PlayerTextureRepository.saveTexture(
                        packet.playerTextures
                    ).insertedCount > 0
                )
            )
        }
}