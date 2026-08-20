package dev.slne.surf.parkour.minestom.listener

import com.google.inject.Inject
import dev.slne.minestom.lobby.api.event.EventRegistrar
import dev.slne.minestom.lobby.api.extension.addListener
import dev.slne.surf.parkour.core.client.service.ParkourConnectionService
import dev.slne.surf.parkour.core.client.service.ParkourMoveService
import net.minestom.server.coordinate.Point
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.event.player.PlayerMoveEvent
import net.minestom.server.event.player.PlayerSpawnEvent

class ParkourListener @Inject constructor() : EventRegistrar {
    override fun register(node: EventNode<Event>) {
        node.addListener<PlayerMoveEvent> { event ->
            val from = event.player.position
            val to = event.newPosition

            if (!hasChangedBlock(from, to)) return@addListener

            ParkourMoveService.processMove(event.player.uuid, to.x(), to.y(), to.z())
        }

        node.addListener<PlayerSpawnEvent> { event ->
            if (!event.isFirstSpawn) return@addListener

            ParkourConnectionService.onJoin(event.player.uuid)
        }

        node.addListener<PlayerDisconnectEvent> { event ->
            ParkourConnectionService.onQuit(event.player.uuid)
        }
    }

    private fun hasChangedBlock(from: Point, to: Point) =
        from.blockX() != to.blockX() ||
                from.blockY() != to.blockY() ||
                from.blockZ() != to.blockZ()
}
