package dev.slne.surf.parkour.util

import dev.slne.surf.parkour.api.entity.ParkourPlayer
import dev.slne.surf.parkour.core.service.parkourPlayerService
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
import org.bukkit.OfflinePlayer
import org.bukkit.event.player.PlayerEvent

suspend fun OfflinePlayer.parkourPlayer(): ParkourPlayer =
    parkourPlayerService.getPlayer(this.uniqueId)

suspend fun PlayerEvent.parkourPlayer() = player.parkourPlayer()

fun SurfComponentBuilder.appendLocalPrefix() = append {
    spacer(">>")
    appendSpace()
    text("Parkour", Colors.PREFIX_COLOR)
    appendSpace()
    spacer("|")
    appendSpace()
}