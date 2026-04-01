package dev.slne.surf.parkour.microservice.rabbit.handler

import dev.slne.surf.parkour.core.common.rabbit.packet.request.LoadAllParkourStatsRequestPacket
import dev.slne.surf.parkour.core.common.rabbit.packet.request.LoadPlayerStatsRequestPacket
import dev.slne.surf.parkour.core.common.rabbit.packet.request.SaveRunRequestPacket
import dev.slne.surf.parkour.core.common.rabbit.packet.response.ManyParkourStatsResponsePacket
import dev.slne.surf.parkour.core.common.rabbit.packet.response.SingleParkourStatResponsePacket
import dev.slne.surf.parkour.microservice.repository.ParkourStatsRepository
import dev.slne.surf.rabbitmq.api.handler.RabbitHandler
import dev.slne.surf.rabbitmq.api.packet.standard.response.primitive.PrimitiveResponse
import kotlinx.coroutines.launch

object ParkourRunsHandler {
    @RabbitHandler
    fun handleLoadAllParkourStatsRequestPacket(
        packet: LoadAllParkourStatsRequestPacket
    ) = packet.launch {
        packet.respond(ManyParkourStatsResponsePacket(ParkourStatsRepository.fetchAllStats()))
    }

    @RabbitHandler
    fun handleLoadPlayerStatsRequestPacket(
        packet: LoadPlayerStatsRequestPacket
    ) = packet.launch {
        packet.respond(SingleParkourStatResponsePacket(ParkourStatsRepository.loadPlayerStats(packet.playerUuid)))
    }

    @RabbitHandler
    fun handleSavePlayerStatsRequestPacket(
        packet: SaveRunRequestPacket
    ) = packet.launch {
        packet.respond(PrimitiveResponse.BooleanResponsePacket(ParkourStatsRepository.saveRun(packet.run).insertedCount > 0))
    }
}