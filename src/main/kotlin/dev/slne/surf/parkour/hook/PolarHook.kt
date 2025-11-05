package dev.slne.surf.parkour.hook

import dev.slne.surf.parkour.service.parkourService
import dev.slne.surf.parkour.util.anyOfType
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

        return generator.blockLocations
            .anyOfType<Vector> { it.equals(player.location.clone().add(0.0, -1.0, 0.0).block) }
    }
}