package dev.slne.surf.parkour.util

import org.bukkit.Bukkit
import java.util.UUID

fun UUID.playerName(): String {
    return Bukkit.getPlayer(this)?.name ?: "Unknown"
}