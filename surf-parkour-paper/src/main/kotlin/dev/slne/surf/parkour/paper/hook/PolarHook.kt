package dev.slne.surf.parkour.paper.hook

import dev.slne.surf.parkour.paper.service.parkourService
import dev.slne.surf.parkour.paper.util.anyOfType
import dev.slne.surf.parkour.paper.util.getBlocksBelowFeet
import dev.slne.surf.parkour.paper.util.isSameBlock
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import top.polar.api.PolarApiAccessor
import top.polar.api.user.event.MitigationEvent
import top.polar.api.user.event.type.CheckType
import java.util.function.Consumer
import kotlin.jvm.optionals.getOrNull

class PolarHook : Consumer<MitigationEvent> {
    override fun accept(event: MitigationEvent) {
        val player = event.user().bukkitPlayer().getOrNull() ?: return

        if (!isStandingOnJumpBlock(player)) {
            return
        }

        if (event.check().type() != CheckType.MOVEMENT) {
            return
        }

        event.cancelled(true)
    }

    fun register() {
        if (!Bukkit.getPluginManager().isPluginEnabled("Polar")) {
            return
        }

        val api = PolarApiAccessor.access().get() ?: return

        api.events().repository().registerListener(
            MitigationEvent::class.java,
            this::accept
        )
    }

    private fun isStandingOnJumpBlock(player: Player): Boolean {
        val playerParkour = parkourService.getParkour(player) ?: return false
        val generator = playerParkour.generators[player.uniqueId] ?: return false

        // Get the blocks below the player's feet (checking corners to handle edge cases)
        val blocksBelow = player.getBlocksBelowFeet()

        return generator.blockLocations
            .anyOfType<Vector> { jumpBlock ->
                blocksBelow.any { blockBelow ->
                    jumpBlock.isSameBlock(blockBelow)
                }
            }
    }
}