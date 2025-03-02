package dev.slne.surf.parkour.command.subcommand.setting

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.LocationArgument
import dev.jorel.commandapi.arguments.WorldArgument
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.parkour.service.JumpAndRunService
import dev.slne.surf.parkour.util.Colors
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.util.BoundingBox
import org.bukkit.util.Vector

class ParkourSettingAreaCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("jumpandrun.command.setting.area")
        withArguments(LocationArgument("pos1"), LocationArgument("pos2"))
        withOptionalArguments(WorldArgument("world"))

        executesPlayer(PlayerCommandExecutor { player: Player, args: CommandArguments ->
            val pos1: Location = args.getUnchecked("pos1") ?: return@PlayerCommandExecutor
            val pos2: Location = args.getUnchecked("pos2") ?: return@PlayerCommandExecutor

            val max = Vector(pos1.x, pos1.y, pos1.z)
            val min = Vector(pos2.x, pos2.y, pos2.z)

            JumpAndRunService.jumpAndRun.boundingBox = BoundingBox.of(min, max)
            JumpAndRunService.jumpAndRun.world = args.getUnchecked("world")

            player.sendMessage(Colors.PREFIX.append(Component.text("Du hast die Arena erfolgreich neu definiert.")))
        })
    }
}
