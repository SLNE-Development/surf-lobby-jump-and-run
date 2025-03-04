package dev.slne.surf.parkour.command

import com.github.shynixn.mccoroutine.bukkit.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.parkour.SurfParkour
import dev.slne.surf.parkour.command.subcommand.*
import dev.slne.surf.parkour.command.subcommand.setting.ParkourMaterialCommand
import dev.slne.surf.parkour.command.subcommand.setting.ParkourSettingCommand
import dev.slne.surf.parkour.database.DatabaseProvider
import dev.slne.surf.parkour.gui.ParkourGUI
import dev.slne.surf.parkour.parkour.Parkour
import dev.slne.surf.parkour.util.MessageBuilder
import org.bukkit.entity.Player

class ParkourCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("jumpandrun.command")

        withSubcommand(ParkourSettingCommand("setting"))
        withSubcommand(ParkourStartCommand("start"))
        withSubcommand(ParkourListCommand("list"))
        withSubcommand(ParkourStatsCommand("stats"))
        withSubcommand(ParkourToggleSoundCommand("toggleSound"))
        withSubcommand(ParkourMaterialCommand("material"))
        withSubcommand(ParkourCreateCommand("create"))
        withSubcommand(ParkourRemoveCommand("remove"))

        executesPlayer(PlayerCommandExecutor() { player: Player, _: CommandArguments ->
            ParkourGUI().createGUI(player)
        })
    }
}

