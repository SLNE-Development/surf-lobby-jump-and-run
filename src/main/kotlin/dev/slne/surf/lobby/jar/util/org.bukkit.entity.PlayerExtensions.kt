package dev.slne.surf.lobby.jar.util

import dev.slne.surf.lobby.jar.player.JumpAndRunPlayerManager
import org.bukkit.entity.Player

suspend fun Player.toJnrPlayer() = JumpAndRunPlayerManager.get(this.uniqueId)