package dev.slne.surf.parkour.util

import dev.slne.surf.parkour.api.entity.ParkourPlayer
import dev.slne.surf.parkour.core.factory.parkourPlayerFactory
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerEvent

fun Player.parkourPlayer(): ParkourPlayer = parkourPlayerFactory.from(this)
fun OfflinePlayer.parkourPlayer(): ParkourPlayer = parkourPlayerFactory.from(this)
fun PlayerEvent.parkourPlayer() = player.parkourPlayer()

fun SurfComponentBuilder.appendLocalPrefix() = append {
    spacer(">>")
    appendSpace()
    text("Parkour", Colors.PREFIX_COLOR)
    appendSpace()
    spacer("|")
    appendSpace()
}