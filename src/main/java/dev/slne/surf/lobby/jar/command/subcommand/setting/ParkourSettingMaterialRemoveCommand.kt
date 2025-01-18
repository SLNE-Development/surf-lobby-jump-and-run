package dev.slne.surf.lobby.jar.command.subcommand.setting

import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.lobby.jar.JumpAndRunService
import dev.slne.surf.lobby.jar.PluginInstance
import dev.slne.surf.lobby.jar.command.argument.MaterialArgument.argument
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player

class ParkourSettingMaterialRemoveCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("jumpandrun.command.setting.removematerial")
        withArguments(argument("material"))

        executesPlayer(PlayerCommandExecutor { player: Player, args: CommandArguments ->
            val material = args.getUnchecked<Material>("material") ?: throw CommandAPI.failWithString("Das Material wurde nicht gefunden.")

            JumpAndRunService.jumpAndRun.materials.remove(material)

            player.sendMessage(PluginInstance.prefix.append(Component.text(String.format("Du hast %s aus der Liste der Materialien entfernt.", material.name))))
        })
    }
}
