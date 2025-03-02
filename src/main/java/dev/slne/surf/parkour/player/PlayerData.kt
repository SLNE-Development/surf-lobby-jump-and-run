package dev.slne.surf.parkour.player

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