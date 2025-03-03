package dev.slne.surf.parkour.command.subcommand

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.LocationArgument
import dev.jorel.commandapi.arguments.RotationArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.arguments.WorldArgument
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.jorel.commandapi.wrappers.Rotation

import dev.slne.surf.parkour.SurfParkour
import dev.slne.surf.parkour.database.DatabaseProvider
import dev.slne.surf.parkour.parkour.Parkour
import dev.slne.surf.parkour.util.Area
import dev.slne.surf.parkour.util.MessageBuilder
import dev.slne.surf.parkour.util.Permission

import it.unimi.dsi.fastutil.objects.ObjectArraySet

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World

import java.util.*

class ParkourCreateCommand(commandName: String): CommandAPICommand(commandName) {
    init {
        withPermission(Permission.COMMAND_PARKOUR_CREATE)
        withArguments(
            StringArgument("name"),
            WorldArgument("world"),
            LocationArgument("min"),
            LocationArgument("max"),
            LocationArgument("start"),
            LocationArgument("respawn"),
            RotationArgument("rotation")
        )

        executesPlayer(PlayerCommandExecutor { player, args ->
            val name = args.getUnchecked<String>("name") ?: return@PlayerCommandExecutor
            val world = args.getUnchecked<World>("world") ?: return@PlayerCommandExecutor
            val min = args.getUnchecked<Location>("min") ?: return@PlayerCommandExecutor
            val max = args.getUnchecked<Location>("max") ?: return@PlayerCommandExecutor
            val start = args.getUnchecked<Location>("start") ?: return@PlayerCommandExecutor
            val respawn = args.getUnchecked<Location>("respawn") ?: return@PlayerCommandExecutor
            val rotation = args.getUnchecked<Rotation>("rotation") ?: return@PlayerCommandExecutor

            val parkour = Parkour(
                UUID.randomUUID(),
                name,
                world,
                Area(min.toVector(), max.toVector()),
                start.toVector(),
                respawn.setRotation(rotation.yaw, rotation.pitch).toVector(),
                ObjectArraySet.of(Material.RED_STAINED_GLASS),
                ObjectArraySet()
            )

            DatabaseProvider.getParkours().add(parkour)

            SurfParkour.send(player, MessageBuilder().primary("Du hast den Parkour ").info(name).primary(" erstellt."))
        })
    }
}