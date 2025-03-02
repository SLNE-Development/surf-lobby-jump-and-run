package dev.slne.surf.parkour.player

import dev.slne.surf.parkour.database.DatabaseProvider
import java.util.UUID

data class PlayerData (
    val uuid: UUID,
    var name: String = "Unknown",
    var highScore: Int = 0,
    var points: Int = 0,
    var trys: Int = 0,
    var likesSound: Boolean = true
) {
    fun edit(block: PlayerData.() -> Unit) {
        block()
        DatabaseProvider.updatePlayerData(this)
    }
}