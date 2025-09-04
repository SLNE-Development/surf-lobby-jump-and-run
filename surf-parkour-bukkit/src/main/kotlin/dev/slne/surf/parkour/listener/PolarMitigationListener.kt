package dev.slne.surf.parkour.listener

import dev.slne.surf.parkour.core.service.parkourService
import dev.slne.surf.parkour.plugin
import dev.slne.surf.parkour.util.appendLocalPrefix
import dev.slne.surf.parkour.util.parkourPlayer
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import top.polar.api.PolarApiAccessor
import top.polar.api.user.event.MitigationEvent
import top.polar.api.user.event.type.CheckType
import java.util.function.Consumer
import kotlin.jvm.optionals.getOrNull

class PolarMitigationListener : Consumer<MitigationEvent> {
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

        plugin.componentLogger.info(buildText {
            appendLocalPrefix()
            success("Hooked into the Polar Bear!")
        })
    }

    private fun isStandingOnJumpBlock(player: Player): Boolean {
        val playerParkour = parkourService.getParkour(player.parkourPlayer()) ?: return false
        val generator = playerParkour.generators[player.uniqueId] ?: return false

        return generator.getRegisteredBlocks()
            .any { it.equals(player.location.clone().add(0.0, -1.0, 0.0).block) }
    }
}