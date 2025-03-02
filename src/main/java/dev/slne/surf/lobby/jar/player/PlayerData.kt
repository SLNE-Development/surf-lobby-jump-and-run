package dev.slne.surf.lobby.jar.player

import java.util.UUID

data class PlayerData (
    val uuid: UUID,
    val name: String,
    val highScore: Int,
    val points: Int,
    val trys: Int,
    val likesSound: Boolean
) {
}