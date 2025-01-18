package dev.slne.surf.lobby.jar.command.subcommand.setting

import com.sk89q.worldedit.IncompleteRegionException
import com.sk89q.worldedit.bukkit.WorldEditPlugin
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.lobby.jar.JumpAndRun
import dev.slne.surf.lobby.jar.PluginInstance
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.entity.Player

class ParkourSettingAreaCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("jumpandrun.command.setting.area")

        executesPlayer(PlayerCommandExecutor { player: Player, args: CommandArguments? ->
            if (!PluginInstance.instance().worldedit()) {
                player.sendMessage(
                    PluginInstance.prefix()
                        .append(Component.text("Bitte installiere WorldEdit um diesen Command auszuführen."))
                )
                return@executesPlayer
            }
            val worldEditPlugin: WorldEditPlugin = PluginInstance.instance().worldEditInstance()

            if (worldEditPlugin == null) {
                player.sendMessage(
                    PluginInstance.prefix().append(Component.text("WorldEdit ist nicht verfügbar!"))
                )
                return@executesPlayer
            }

            val session = worldEditPlugin.getSession(player)
            try {
                val region = session.getSelection(session.selectionWorld)


                val pos1 = region.minimumPoint
                val pos2 = region.maximumPoint

                val jumpAndRun: JumpAndRun =
                    PluginInstance.instance().jumpAndRunProvider().jumpAndRun()
                jumpAndRun.setPosOne(
                    Location(
                        player.world,
                        pos1.x.toDouble(),
                        pos1.y.toDouble(),
                        pos1.z.toDouble()
                    )
                )
                jumpAndRun.setPosTwo(
                    Location(
                        player.world,
                        pos2.x.toDouble(),
                        pos2.y.toDouble(),
                        pos2.z.toDouble()
                    )
                )

                player.sendMessage(
                    PluginInstance.prefix()
                        .append(Component.text("Du hast die Arena erfolgreich neu definiert."))
                )
            } catch (e: IncompleteRegionException) {
                player.sendMessage(
                    PluginInstance.prefix()
                        .append(Component.text("Bitte wähle einen Bereich mit WorldEdit aus!"))
                )
            }
        })
    }
}
