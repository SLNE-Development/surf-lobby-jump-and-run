package dev.slne.surf.parkour.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.parkour.parkourConfig
import dev.slne.surf.parkour.permission.ParkourPermissionRegistry
import dev.slne.surf.parkour.plugin
import dev.slne.surf.surfapi.bukkit.api.util.forEachPlayer
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun CommandAPICommand.parkourReloadCommand() = subcommand("reload") {
    withPermission(ParkourPermissionRegistry.COMMAND_PARKOUR_RELOAD)
    anyExecutor { executor, _ ->
        executor.sendText {
            appendPrefix()
            info("Das Plugin wird neu geladen...")
        }

        forEachPlayer {
            it.inventory.remove(plugin.inventoryItem)
        }

        parkourConfig.reload()

        forEachPlayer {
            it.inventory.setItem(6, plugin.inventoryItem)
        }

        executor.sendText {
            appendPrefix()
            success("Das Plugin wurde neu geladen.")
        }
    }
}