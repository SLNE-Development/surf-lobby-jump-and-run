package dev.slne.surf.lobby.jar.command.subcommand.setting

import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.lobby.jar.PluginInstance
import dev.slne.surf.lobby.jar.command.argument.MaterialArgument.argument
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player

class ParkourSettingMaterialAddCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("jumpandrun.command.setting.addmaterial")
        withArguments(argument("material"))

        executesPlayer(PlayerCommandExecutor { player: Player, args: CommandArguments ->
            val material =
                args.getUnchecked<Material>("material")
                    ?: throw CommandAPI.failWithString("Das Material wurde nicht gefunden.")
            PluginInstance.instance().jumpAndRunProvider().jumpAndRun().getMaterials().add(material)
            player.sendMessage(
                PluginInstance.prefix().append(
                    Component.text(
                        String.format(
                            "Du hast %s zur Liste der Materialien hinzugefügt.",
                            material.name
                        )
                    )
                )
            )
        })
    }
}
