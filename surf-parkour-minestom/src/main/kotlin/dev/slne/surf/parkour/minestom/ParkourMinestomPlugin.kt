package dev.slne.surf.parkour.minestom

import com.google.auto.service.AutoService
import dev.slne.minestom.lobby.api.plugin.MinestomPlugin
import dev.slne.minestom.lobby.api.plugin.annotation.MinestomPluginMeta
import dev.slne.surf.parkour.minestom.command.ParkourCommandRegistrar
import dev.slne.surf.parkour.minestom.listener.ParkourListener

@AutoService(MinestomPlugin::class)
@MinestomPluginMeta(
    "surf-parkour-minestom",
    dependsOn = [
        "surf-api-minestom",
        "surf-rabbitmq-minestom"
    ]
)
class ParkourMinestomPlugin : MinestomPlugin(ParkourMinestomEntrypoint::class.java) {
    override fun configurePlugin() {
        bindEventRegistrar<ParkourListener>()
        bindCommandRegistrar<ParkourCommandRegistrar>()
    }
}
