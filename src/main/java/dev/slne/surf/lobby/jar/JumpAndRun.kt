package dev.slne.surf.lobby.jar

import it.unimi.dsi.fastutil.objects.*
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
    private val displayName: String = "Parkour"

    var posOne: Location? = null
    var posTwo: Location? = null
    var spawn: Location? = null
    var start: Location? = null

    val players: ObjectSet<Player> = ObjectArraySet()
    val materials: ObjectList<Material> = ObjectArrayList()
    val latestBlocks: Object2ObjectMap<Player, Block> = Object2ObjectOpenHashMap()

    fun kick(player: Player) {
        JumpAndRunService.remove(player)
    }

    fun join(player: Player) {
        JumpAndRunService.start(player)
    }
}
