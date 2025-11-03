package dev.slne.surf.parkour

import dev.slne.surf.parkour.command.parkourCommand

object BukkitCommandManager {
    fun registerCommands() {
        parkourCommand()
    }
}