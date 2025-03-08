package dev.slne.surf.parkour.command.subcommand.setting

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.LocationArgument
import dev.jorel.commandapi.arguments.WorldArgument
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.parkour.SurfParkour
import dev.slne.surf.parkour.command.argument.ParkourArgument
import dev.slne.surf.parkour.parkour.Parkour
import dev.slne.surf.parkour.util.Area
import dev.slne.surf.parkour.util.Colors
import dev.slne.surf.parkour.util.MessageBuilder
import dev.slne.surf.parkour.util.Permission
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.util.BoundingBox
import org.bukkit.util.Vector

class ParkourSettingAreaCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(Permission.COMMAND_PARKOUR_SETTING_AREA)
        withArguments(
            ParkourArgument("parkour"),
            LocationArgument("pos1"),
            LocationArgument("pos2")
        )
        withOptionalArguments(WorldArgument("world"))

        executesPlayer(PlayerCommandExecutor { player: Player, args: CommandArguments ->
            val pos1: Location = args.getUnchecked("pos1") ?: return@PlayerCommandExecutor
            val pos2: Location = args.getUnchecked("pos2") ?: return@PlayerCommandExecutor
            val parkour = args.getUnchecked<Parkour>("parkour") ?: return@PlayerCommandExecutor
            val world = args.getUnchecked<World>("world") ?: parkour.world

            val max = Vector(pos1.x, pos1.y, pos1.z)
            val min = Vector(pos2.x, pos2.y, pos2.z)

            parkour.edit {
                this.area = Area(max, min)
                this.world = world
            }

            SurfParkour.send(player, MessageBuilder().primary("Du hast die Arena von ").info(parkour.name).primary(" neu definiert."))
        })
    }
}
