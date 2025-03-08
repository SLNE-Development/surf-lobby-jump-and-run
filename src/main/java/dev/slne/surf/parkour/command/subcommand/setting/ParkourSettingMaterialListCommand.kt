package dev.slne.surf.parkour.command.subcommand.setting

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.IntegerArgument
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.parkour.SurfParkour

import dev.slne.surf.parkour.command.argument.ParkourArgument
import dev.slne.surf.parkour.parkour.Parkour
import dev.slne.surf.parkour.util.MessageBuilder
import dev.slne.surf.parkour.util.PageableMessageBuilder
import dev.slne.surf.parkour.util.Permission

import org.bukkit.entity.Player

class ParkourSettingMaterialListCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(Permission.COMMAND_PARKOUR_SETTING_MATERIAL_LIST)
        withArguments(ParkourArgument("parkour"))
        withOptionalArguments(IntegerArgument("page"))
        executesPlayer(PlayerCommandExecutor { player: Player, args: CommandArguments ->
            val parkour = args.getUnchecked<Parkour>("parkour") ?: return@PlayerCommandExecutor
            val page = args.getOrDefaultUnchecked("page", 1)
            val message = PageableMessageBuilder()

            if(parkour.availableMaterials.isEmpty()) {
                SurfParkour.send(player, MessageBuilder().error("Es sind keine Material-Typen in ").info(parkour.name).error(" eingestellt").primary("."))
                return@PlayerCommandExecutor
            }

            message.setPageCommand("/parkour material list ${parkour.name} %page%")
            message.setTitle(MessageBuilder().primary("Materialien von ").info(parkour.name).build())

            for (availableMaterial in parkour.availableMaterials) {
                message.addLine(MessageBuilder().darkSpacer("- ").variableValue(availableMaterial.name).build())
            }

            message.send(player, page)
        })
    }
}
