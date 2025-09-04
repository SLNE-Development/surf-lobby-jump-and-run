package dev.slne.surf.parkour.fallback.model

import dev.slne.surf.parkour.api.entity.ParkourPlayer
import dev.slne.surf.parkour.api.event.ParkourFailEvent
import dev.slne.surf.parkour.api.event.ParkourSuccessEvent
import dev.slne.surf.parkour.api.model.parkour.Parkour
import dev.slne.surf.parkour.api.model.parkour.ParkourGenerator
import dev.slne.surf.parkour.core.generator.DefaultParkourGenerator
import dev.slne.surf.parkour.core.model.CoreParkourStatistic
import dev.slne.surf.parkour.core.registry.parkourRegistry
import dev.slne.surf.parkour.core.service.parkourStatisticsService
import dev.slne.surf.surfapi.core.api.messages.CommonComponents
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import it.unimi.dsi.fastutil.objects.ObjectSet
import org.bukkit.Location
import org.bukkit.Material
import java.util.*

class FallbackParkour(
    override val uuid: UUID,
    override val name: String,
    override val players: ObjectSet<ParkourPlayer> = mutableObjectSetOf(),
    override val generators: Object2ObjectMap<UUID, ParkourGenerator> = mutableObject2ObjectMapOf(),
    override val playerTimes: Object2ObjectMap<UUID, Long>,
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
        playerTimes[player.uuid] = System.currentTimeMillis()
        this@FallbackParkour.generators[player.uuid] = generator
        generator.start()
    }

    override suspend fun onFailure(player: ParkourPlayer) {
        val bukkitPlayer = player.player() ?: return
        val generator = generators[player.uuid] ?: return
        val time =
            System.currentTimeMillis() - (playerTimes[player.uuid] ?: System.currentTimeMillis())

        generator.stop()
        bukkitPlayer.teleportAsync(spawnLocation)

        generators.remove(player.uuid)
        players.remove(player)
        playerTimes.remove(player.uuid)

        ParkourFailEvent(this, player).callEvent()

        parkourStatisticsService.addStatistic(
            CoreParkourStatistic(
                player.uuid,
                this,
                time,
                generator.currentIndex()
            )
        )
    }

    override suspend fun onSuccess(player: ParkourPlayer, index: Int) {
        val bukkitPlayer = player.player() ?: return
        val generator = generators[player.uuid] ?: return

        bukkitPlayer.sendActionBar {
            buildText {
                variableValue(generator.currentIndex())
                appendSpace()
                append(CommonComponents.EM_DASH)
                appendSpace()
                success("+1")
            }
        }

        generator.generate()
        ParkourSuccessEvent(this, player, index).callEvent()
    }

    override fun modify(block: Parkour.() -> Unit) = block(this)
    override fun modifySaving(block: Parkour.() -> Unit) {
        block(this)
        parkourRegistry.registerParkour(this)
    }
}