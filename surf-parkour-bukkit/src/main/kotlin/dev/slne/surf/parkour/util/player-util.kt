package dev.slne.surf.parkour.util

import dev.slne.surf.parkour.api.entity.ParkourPlayer
import dev.slne.surf.parkour.core.factory.parkourPlayerFactory
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerEvent

fun Player.parkourPlayer(): ParkourPlayer = parkourPlayerFactory.from(this)
fun OfflinePlayer.parkourPlayer(): ParkourPlayer = parkourPlayerFactory.from(this)
fun PlayerEvent.parkourPlayer() = player.parkourPlayer()