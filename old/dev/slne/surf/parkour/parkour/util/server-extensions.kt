package dev.slne.surf.parkour.util

import com.github.retrooper.packetevents.util.Vector3i
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Server
import org.bukkit.block.Block

fun Server.sendBlockChange(location: Location, material: Material) =
    Bukkit.getOnlinePlayers().forEach { it.sendBlockChange(location, material.createBlockData()) }

fun Server.sendBlockChanges(vararg pairs: Pair<Location, Material>) =
    pairs.forEach { this.sendBlockChange(it.first, it.second) }

fun Location.equalsBlockLocation(other: Location) = this.world == other.world &&
        this.blockX == other.blockX && this.blockY == other.blockY && this.blockZ == other.blockZ

fun Block.equalsVector3i(vector: Vector3i): Boolean {
    return this.x == vector.x &&
            this.y == vector.y &&
            this.z == vector.z
}