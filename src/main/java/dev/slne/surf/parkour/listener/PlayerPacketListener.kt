package dev.slne.surf.parkour.listener

import com.github.retrooper.packetevents.event.PacketListener
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement
import dev.slne.surf.parkour.parkour.Parkour
import org.bukkit.entity.Player

class PlayerPacketListener : PacketListener {
    override fun onPacketReceive(event: PacketReceiveEvent) {
        val player = event.getPlayer<Player>()

        if (event.packetType != PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
            return
        }

        val packet = WrapperPlayClientPlayerBlockPlacement(event)
        val parkour = Parkour.getParkour(player)

        if (parkour == null) {
            return
        }

        val latestJumps = parkour.latestJumps[player.uniqueId] ?: return

        latestJumps.any {
            it?.location?.blockX == packet.blockPosition.x
                    && it.location.blockY == packet.blockPosition.y
                    && it.location.blockZ == packet.blockPosition.z
        }.also {
            if (it) {
                event.isCancelled = true
            }
        }
    }
}