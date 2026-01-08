package dev.slne.surf.parkour.paper.command

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.parkour.paper.menu.ParkourMenu
import dev.slne.surf.parkour.paper.model.parkour.PersonalParkourSummary
import dev.slne.surf.parkour.paper.permission.ParkourPermissionRegistry
import dev.slne.surf.parkour.paper.plugin
import dev.slne.surf.parkour.paper.service.parkourService
import kotlinx.coroutines.withContext

fun parkourCommand() = commandAPICommand("parkour") {
    withPermission(ParkourPermissionRegistry.COMMAND_PARKOUR)
    parkourPlayCommand()
    parkourListCommand()
    parkourReloadCommand()
    parkourCreateCommand()
    parkourStatsCommand()

    playerExecutor { player, _ ->
        plugin.launch {
            val stats = parkourService.getRuns(player.uniqueId)
            val summary = PersonalParkourSummary(player.uniqueId, stats)
            summary.initName()

            withContext(plugin.entityDispatcher(player)) {
                ParkourMenu(summary).open(player)
            }
        }
    }
}