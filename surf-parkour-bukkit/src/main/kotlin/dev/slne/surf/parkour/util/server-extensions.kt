package dev.slne.surf.parkour.util

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Server

fun Server.sendBlockChange(location: Location, material: Material) =
    Bukkit.getOnlinePlayers().forEach { it.sendBlockChange(location, material.createBlockData()) }

fun Server.sendBlockChanges(vararg pairs: Pair<Location, Material>) =
    pairs.forEach { this.sendBlockChange(it.first, it.second) }