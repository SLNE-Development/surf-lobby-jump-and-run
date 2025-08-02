package dev.slne.surf.parkour.api.entity

import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.*

interface ParkourPlayer {
    val uuid: UUID
    val name: String

    fun player(): Player?
    fun offlinePlayer(): OfflinePlayer

    fun sendText(block: SurfComponentBuilder.() -> Unit) = player()?.sendText { block() }
}