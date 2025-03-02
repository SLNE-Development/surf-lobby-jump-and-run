package dev.slne.surf.parkour.command.subcommand.setting

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.LocationArgument
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.parkour.service.JumpAndRunService
import dev.slne.surf.parkour.util.Colors
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.entity.Player

class ParkourSettingStartCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("jumpandrun.command.setting.setStart")

        withArguments(LocationArgument("pos"))

        executesPlayer(PlayerCommandExecutor { player: Player, args: CommandArguments ->
            val pos = args.getUnchecked<Location>("pos") ?: return@PlayerCommandExecutor

            JumpAndRunService.jumpAndRun.start = pos.toVector().normalize()

            player.sendMessage(Colors.PREFIX.append(Component.text("Du hast den Start erfolgreich neu definiert.")))
        })
    }
}
