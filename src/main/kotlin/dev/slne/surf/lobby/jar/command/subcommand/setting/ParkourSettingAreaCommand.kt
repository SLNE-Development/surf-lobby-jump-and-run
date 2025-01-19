package dev.slne.surf.lobby.jar.command.subcommand.setting

import com.sk89q.worldedit.IncompleteRegionException
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.lobby.jar.plugin
import dev.slne.surf.lobby.jar.service.JumpAndRun
import dev.slne.surf.lobby.jar.service.JumpAndRunService
import dev.slne.surf.lobby.jar.util.prefix
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.util.BoundingBox

class ParkourSettingAreaCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("jumpandrun.command.setting.area")

        executesPlayer(PlayerCommandExecutor { player: Player, _: CommandArguments? ->
            if (!plugin.worldedit) {
                player.sendMessage(prefix.append(Component.text("Bitte installiere WorldEdit um diesen Command auszuführen.")))

                return@PlayerCommandExecutor
            }

            val worldEditPlugin = plugin.worldEditInstance ?: return@PlayerCommandExecutor
            val session = worldEditPlugin.getSession(player)

            try {
                val region = session.getSelection(session.selectionWorld)


                val pos1 = region.minimumPoint
                val pos2 = region.maximumPoint

                val jumpAndRun: JumpAndRun = JumpAndRunService.jumpAndRun

                jumpAndRun.world = player.world
                jumpAndRun.boundingBox = BoundingBox(
                    pos1.x.toDouble(),
                    pos1.y.toDouble(),
                    pos1.z.toDouble(),
                    pos2.x.toDouble(),
                    pos2.y.toDouble(),
                    pos2.z.toDouble()
                )

                player.sendMessage(prefix.append(Component.text("Du hast die Arena erfolgreich neu definiert.")))
            } catch (e: IncompleteRegionException) {
                player.sendMessage(prefix.append(Component.text("Bitte wähle einen Bereich mit WorldEdit aus!")))
            }
        })
    }
}
