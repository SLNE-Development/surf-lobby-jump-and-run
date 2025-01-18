package dev.slne.surf.lobby.jar

import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import it.unimi.dsi.fastutil.objects.ObjectList
import lombok.Builder
import lombok.Getter
import lombok.Setter
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player

@Builder
@Getter
@Setter
class JumpAndRun {
    private val displayName: String? = null

    private val posOne: Location? = null
    private val posTwo: Location? = null
    private val spawn: Location? = null
    private val start: Location? = null

    private val players: ObjectList<Player>? = null
    private val materials: ObjectList<Material>? = null
    private val latestBlocks: Object2ObjectMap<Player, Block>? = null

    fun kick(player: Player?) {
        PluginInstance.Companion.instance().jumpAndRunProvider().remove(player)
    }

    fun join(player: Player?) {
        PluginInstance.Companion.instance().jumpAndRunProvider().start(player)
    }
}
