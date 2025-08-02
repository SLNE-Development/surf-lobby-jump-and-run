package dev.slne.surf.parkour.listener

import com.github.retrooper.packetevents.event.PacketListener
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.util.Vector3i
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement

import dev.slne.surf.parkour.core.service.parkourService
import dev.slne.surf.parkour.util.parkourPlayer

import org.bukkit.block.Block
import org.bukkit.entity.Player

class ParkourPacketListener : PacketListener {
    override fun onPacketReceive(event: PacketReceiveEvent) {
        val player = event.getPlayer<Player>()

        if (event.packetType != PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
            return
        }

        val packet = WrapperPlayClientPlayerBlockPlacement(event)
        val playerParkour = parkourService.getParkour(player.parkourPlayer()) ?: return
        val generator = playerParkour.generator[player.uniqueId] ?: return

        generator.currentBlock()?.let {
            if (it.equalsVector3i(packet.blockPosition)) {
                event.isCancelled = true
            }
        }

        generator.targetBlock()?.let {
            if (it.equalsVector3i(packet.blockPosition)) {
                event.isCancelled = true
            }
        }

        generator.nextBlock()?.let {
            if (it.equalsVector3i(packet.blockPosition)) {
                event.isCancelled = true
            }
        }
    }
}

fun Block.equalsVector3i(vector: Vector3i): Boolean {
    return this.x == vector.x &&
            this.y == vector.y &&
            this.z == vector.z
}