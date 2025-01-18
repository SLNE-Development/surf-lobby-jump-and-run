package dev.slne.surf.lobby.jar.service

import it.unimi.dsi.fastutil.objects.*
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player

class JumpAndRun {
    var displayName: String = "Parkour"

    var posOne: Location? = null
    var posTwo: Location? = null
    var spawn: Location? = null
    var start: Location? = null

    var players: ObjectSet<Player> = ObjectArraySet()
    var materials: ObjectList<Material> = ObjectArrayList()
    var latestBlocks: Object2ObjectMap<Player, Block> = Object2ObjectOpenHashMap()

    fun kick(player: Player) {
        JumpAndRunService.remove(player)
    }

    fun join(player: Player) {
        JumpAndRunService.start(player)
    }
}