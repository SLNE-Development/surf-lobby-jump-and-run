package dev.slne.surf.parkour.listener

import com.github.retrooper.packetevents.event.PacketListener
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement
import dev.slne.surf.parkour.core.service.parkourService
import dev.slne.surf.parkour.util.equalsVector3i
import dev.slne.surf.parkour.util.parkourPlayer
import org.bukkit.entity.Player

class ParkourPacketListener : PacketListener {
    override fun onPacketReceive(event: PacketReceiveEvent) {
        val player = event.getPlayer<Player>()

        if (event.packetType != PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
            return
        }

        val packet = WrapperPlayClientPlayerBlockPlacement(event)
        val playerParkour = parkourService.getParkour(player.parkourPlayer()) ?: return
        val generator = playerParkour.generators[player.uniqueId] ?: return

        if (generator.getRegisteredBlocks().any { it.equalsVector3i(packet.blockPosition) }) {
            event.isCancelled = true
        }
    }
}