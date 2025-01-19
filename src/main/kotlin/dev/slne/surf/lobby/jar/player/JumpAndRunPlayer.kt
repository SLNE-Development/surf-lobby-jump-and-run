package dev.slne.surf.lobby.jar.player

import dev.slne.surf.lobby.jar.mysql.JumpAndRunPlayerModel
import dev.slne.surf.lobby.jar.mysql.worker.ConnectionWorkers
import dev.slne.surf.lobby.jar.util.PluginColor
import dev.slne.surf.lobby.jar.util.prefix
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import java.time.Duration
import java.util.*

class JumpAndRunPlayer(val uuid: UUID) {

    private lateinit var jumpAndRunPlayerModel: JumpAndRunPlayerModel

    val points get() = jumpAndRunPlayerModel.points
    val trys get() = jumpAndRunPlayerModel.trys
    val sound get() = jumpAndRunPlayerModel.sound
    val highScore get() = jumpAndRunPlayerModel.highScore

    val player get() = Bukkit.getPlayer(uuid)

    suspend fun incrementPoints() {
        if (sound) {
            player?.playSound(
                Sound.sound(
                    org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                    Sound.Source.MASTER,
                    100f,
                    1f
                ), Sound.Emitter.self()
            )
        }

        update { it.points += 1 }
    }

    suspend fun incrementTrys() {
        update { it.trys += 1 }
    }

    suspend fun setSound(sound: Boolean) {
        if (this.sound == sound) return

        update { it.sound = sound }
    }

    suspend fun setHighScore(highScore: Int) {
        if (highScore <= this.highScore) return

        update { it.highScore = highScore }

        val player = player ?: return

        if (sound) {
            player.playSound(
                Sound.sound(
                    org.bukkit.Sound.ITEM_TOTEM_USE,
                    Sound.Source.MASTER,
                    100f,
                    1f
                ), Sound.Emitter.self()
            )
        }

        player.sendMessage(prefix.append(Component.text("Du hast deinen Highscore gebrochen! Dein neuer Highscore ist ${highScore}!")));
        player.showTitle(
            Title.title(
                Component.text("Rekord!", PluginColor.BLUE_MID),
                Component.text(
                    "Du hast einen neuen persönlichen Rekord aufgestellt.",
                    PluginColor.DARK_GRAY
                ),
                Title.Times.times(
                    Duration.ofSeconds(1),
                    Duration.ofSeconds(2),
                    Duration.ofSeconds(1)
                )
            )
        )
    }

    suspend fun fetch() = ConnectionWorkers.async {
        jumpAndRunPlayerModel = JumpAndRunPlayerModel.findFirst("uuid = ?", uuid.toString())
            ?: JumpAndRunPlayerModel(uuid).apply { saveIt() }

        jumpAndRunPlayerModel
    }

    suspend fun update(block: (JumpAndRunPlayerModel) -> Unit) = ConnectionWorkers.async {
        block.invoke(jumpAndRunPlayerModel)
        jumpAndRunPlayerModel.saveIt()

        jumpAndRunPlayerModel
    }

    fun sendMessage(message: Component) {
        player?.sendMessage(message)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is JumpAndRunPlayer) return false

        if (uuid != other.uuid) return false

        return true
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }

}