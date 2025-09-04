package dev.slne.surf.parkour.fallback.model

import dev.slne.surf.parkour.api.entity.ParkourPlayer
import dev.slne.surf.parkour.api.event.ParkourFailEvent
import dev.slne.surf.parkour.api.event.ParkourSuccessEvent
import dev.slne.surf.parkour.api.model.parkour.Parkour
import dev.slne.surf.parkour.api.model.parkour.ParkourGenerator
import dev.slne.surf.parkour.core.generator.DefaultParkourGenerator
import dev.slne.surf.parkour.core.registry.parkourRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import it.unimi.dsi.fastutil.objects.ObjectSet
import org.bukkit.Location
import org.bukkit.Material
import java.util.*

class FallbackParkour(
    override val uuid: UUID,
    override val name: String,
    override val players: ObjectSet<ParkourPlayer>,
    override val generators: Object2ObjectMap<UUID, ParkourGenerator>,
    override val spawnLocation: Location,
    override val corner1: Location,
    override val corner2: Location
) : Parkour {
    override suspend fun start(player: ParkourPlayer) {
        val generator = DefaultParkourGenerator(
            player,
            Material.RED_CONCRETE,
            corner1,
            corner2,
            spawnLocation
        )

        players.add(player)
        this@FallbackParkour.generators[player.uuid] = generator
        generator.start()
    }

    override suspend fun onFailure(player: ParkourPlayer) {
        val bukkitPlayer = player.player() ?: return
        val generator = generators[player.uuid] ?: return

        generator.stop()

        bukkitPlayer.teleportAsync(spawnLocation).thenRun {
            generators.remove(player.uuid)
            players.remove(player)
        }

        player.sendText {
            appendPrefix()
            error("Failed!")
        }

        ParkourFailEvent(this, player).callEvent()
    }

    override suspend fun onSuccess(player: ParkourPlayer, index: Int) {
        val bukkitPlayer = player.player() ?: return
        val generator = generators[player.uuid] ?: return

        generator.generate()

        bukkitPlayer.sendText {
            appendPrefix()
            success("Success!")
        }

        ParkourSuccessEvent(this, player, index).callEvent()
    }

    override fun modify(block: Parkour.() -> Unit) = block(this)
    override fun modifySaving(block: Parkour.() -> Unit) {
        block(this)
        parkourRegistry.registerParkour(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Parkour) return false
        return uuid == other.uuid
    }
}