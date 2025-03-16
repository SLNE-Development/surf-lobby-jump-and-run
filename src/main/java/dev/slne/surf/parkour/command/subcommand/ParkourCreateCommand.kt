package dev.slne.surf.parkour.command.subcommand

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.*
import dev.jorel.commandapi.wrappers.Rotation
import dev.slne.surf.parkour.database.DatabaseProvider
import dev.slne.surf.parkour.parkour.Parkour
import dev.slne.surf.parkour.send
import dev.slne.surf.parkour.util.Area
import dev.slne.surf.parkour.util.Permission
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import java.util.*

class ParkourCreateCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(Permission.COMMAND_PARKOUR_CREATE)

        stringArgument("name")
        worldArgument("world")
        locationArgument("min")
        locationArgument("max")
        locationArgument("start")
        locationArgument("respawn")
        rotationArgument("rotation")

        anyExecutor { sender, args ->
            val name: String by args
            val world: World by args
            val min: Location by args
            val max: Location by args
            val start: Location by args
            val respawn: Location by args
            val rotation: Rotation by args

            val parkour = Parkour(
                uuid = UUID.randomUUID(),
                name = name,
                world = world,
                area = Area(min.toVector(), max.toVector()),
                start = start.toVector(),
                respawn = respawn.setRotation(rotation.yaw, rotation.pitch).toVector(),
                availableMaterials = mutableObjectSetOf(Material.RED_CONCRETE)
            )

            if (DatabaseProvider.getParkours().any { it.name == name }) {
                sender.send {
                    error("Der Parkour ")
                    variableValue(name)
                    error(" existiert bereits.")
                }

                return@anyExecutor
            }

            DatabaseProvider.getParkours().add(parkour)

            sender.sendText {
                appendPrefix()
                spacer("-----------------------------------------------------")
                appendNewPrefixedLine()
                append {
                    primary("Der Parkour ")
                    variableValue(name)
                    primary(" wurde mit folgenden Werten erstellt:").build()
                }
                appendNewPrefixedLine()
                appendNewPrefixedLine()
                append {
                    spacer(" - ")
                    variableKey("Name: ")
                    variableValue(name).build()
                }
                appendNewPrefixedLine()
                append {
                    spacer(" - ")
                    variableKey("Welt: ")
                    variableValue(world.name).build()
                }
                appendNewPrefixedLine()
                append {
                    spacer(" - ")
                    variableKey("Position 1: ")
                    variableValue("${min.blockX}, ${min.blockY}, ${min.blockZ}").build()
                }
                appendNewPrefixedLine()
                append {
                    spacer(" - ")
                    variableKey("Position 2: ")
                    variableValue("${max.blockX}, ${max.blockY}, ${max.blockZ}").build()
                }
                appendNewPrefixedLine()
                append {
                    spacer(" - ")
                    variableKey("Startpunkt: ")
                    variableValue("${start.x}, ${start.y}, ${start.z}").build()
                }
                appendNewPrefixedLine()
                append {
                    spacer(" - ")
                    variableKey("Respawnpunkt: ")
                    variableValue("${respawn.x}, ${respawn.y}, ${respawn.z}").build()
                }
                appendNewPrefixedLine()
                append {
                    spacer(" - ")
                    variableKey("Rotation: ")
                    variableValue("${rotation.yaw}, ${rotation.pitch}").build()
                }
                appendNewPrefixedLine()
                spacer("-----------------------------------------------------")
            }
        }
    }
}