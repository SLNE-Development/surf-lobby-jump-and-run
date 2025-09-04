package dev.slne.surf.parkour.core.util

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Server

fun Server.sendBlockChange(location: Location, material: Material) =
    Bukkit.getOnlinePlayers().forEach { it.sendBlockChange(location, material.createBlockData()) }

fun Server.sendBlockChanges(vararg changes: Pair<Location, Material>) =
    changes.forEach { this.sendBlockChange(it.first, it.second) }

fun Server.sendNullableBlockChanges(vararg changes: Pair<Location?, Material>) =
    changes.forEach { pair ->
        pair.first?.let {
            this.sendBlockChange(it, pair.second)
        }
    }

fun Location.asSimpleString() = "(${x.toInt()}, ${y.toInt()}, ${z.toInt()})"