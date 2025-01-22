package dev.slne.surf.lobby.jar.service

import it.unimi.dsi.fastutil.objects.*
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.util.BoundingBox
import org.bukkit.util.Vector

data class JumpAndRun(
    val displayName: String = "Parkour",
    private var worldName: String?,
    private var posOne: Vector?,
    private var posTwo: Vector?,
    var spawn: Vector?,
    var start: Vector?,
    val players: ObjectSet<Player> = ObjectArraySet(),

    val materials: ObjectList<Material> = ObjectArrayList(),
) {
    var world: World?
        get() = worldName?.let { Bukkit.getWorld(it) }
        set(value) {
            worldName = value?.name
        }

    var boundingBox: BoundingBox
        get() {
            require(posOne != null && posTwo != null) { "Positions not set" }

            return BoundingBox(
                posOne!!.x,
                posOne!!.y,
                posOne!!.z,
                posTwo!!.x,
                posTwo!!.y,
                posTwo!!.z
            )
        }
        set(value) {
            posOne = Vector(value.minX, value.minY, value.minZ)
            posTwo = Vector(value.maxX, value.maxY, value.maxZ)
        }
}