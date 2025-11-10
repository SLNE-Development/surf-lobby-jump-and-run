package dev.slne.surf.parkour.listener

import com.github.retrooper.packetevents.event.PacketListenerAbstract
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement
import dev.slne.surf.parkour.service.parkourService
import dev.slne.surf.parkour.util.anyOfType
import dev.slne.surf.parkour.util.equalsVector3i
import org.bukkit.entity.Player
import org.bukkit.util.Vector

class PlayerPacketListener : PacketListenerAbstract() {
    override fun onPacketReceive(event: PacketReceiveEvent) {
        val player = event.getPlayer<Player>()

        if (event.packetType != PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
            return
        }

        val packet = WrapperPlayClientPlayerBlockPlacement(event)
        val parkour = parkourService.getParkour(player) ?: return
        val generator = parkour.generators[player.uniqueId] ?: return

        generator.blockLocations.anyOfType<Vector> {
            it.equalsVector3i(packet.blockPosition)
        }.also {
            if (it) {
                event.isCancelled = true
            }
        }
    }
}