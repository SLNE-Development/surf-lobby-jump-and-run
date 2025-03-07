package dev.slne.surf.parkour.command.subcommand.setting

import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor

import dev.slne.surf.parkour.SurfParkour
import dev.slne.surf.parkour.command.argument.MaterialArgument.argument
import dev.slne.surf.parkour.command.argument.ParkourArgument
import dev.slne.surf.parkour.parkour.Parkour
import dev.slne.surf.parkour.util.MessageBuilder
import dev.slne.surf.parkour.util.Permission

import org.bukkit.Material
import org.bukkit.entity.Player

class ParkourSettingMaterialRemoveCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(Permission.COMMAND_PARKOUR_SETTING_MATERIAL_REMOVE)
        withArguments(argument("material"))
        withArguments(ParkourArgument("parkour"))

        executesPlayer(PlayerCommandExecutor { player: Player, args: CommandArguments ->
            val material = args.getUnchecked<Material>("material") ?: throw CommandAPI.failWithString("Das Material wurde nicht gefunden.")
            val parkour = args.getUnchecked<Parkour>("parkour") ?: throw CommandAPI.failWithString("Der Parkour wurde nicht gefunden.")

            parkour.edit {
                this.availableMaterials.remove(material)
            }

            SurfParkour.send(player, MessageBuilder().primary("Du hast ").info(material.name).primary(" von der Liste der Materialien von ").info(parkour.name).error(" entfernt").primary("."))
        })
    }
}
