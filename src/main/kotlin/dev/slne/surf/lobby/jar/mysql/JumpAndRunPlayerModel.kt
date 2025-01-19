package dev.slne.surf.lobby.jar.mysql

import org.javalite.activejdbc.CompanionModel
import org.javalite.activejdbc.Model
import java.util.*

@DatabaseModel
data class JumpAndRunPlayerModel(
    val uuid: UUID,
    var points: Int = 0,
    var trys: Int = 0,
    var sound: Boolean = true,
    var highScore: Int = 0
) : Model() {
    companion object : CompanionModel<JumpAndRunPlayerModel>(JumpAndRunPlayerModel::class)
}