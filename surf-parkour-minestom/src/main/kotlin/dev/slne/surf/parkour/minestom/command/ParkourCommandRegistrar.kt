package dev.slne.surf.parkour.minestom.command

import com.google.inject.Inject
import dev.slne.minestom.lobby.api.command.CommandRegistrar

/**
 * Registers the parkour commands of this plugin.
 */
class ParkourCommandRegistrar @Inject constructor() : CommandRegistrar {
    override fun register() {
        parkourCommand()
    }
}
