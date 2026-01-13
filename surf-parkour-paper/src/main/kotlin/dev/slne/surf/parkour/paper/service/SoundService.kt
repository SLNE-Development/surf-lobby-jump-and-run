package dev.slne.surf.parkour.paper.service

import dev.slne.surf.surfapi.core.api.messages.adventure.playSound
import org.bukkit.entity.Player
import net.kyori.adventure.sound.Sound as AdventureSound
import org.bukkit.Sound as BukkitSound

class SoundService {
    fun playSuccess(player: Player) {
        player.playSound(true) {
            type(BukkitSound.ENTITY_CHICKEN_EGG)
            source(AdventureSound.Source.NEUTRAL)
            pitch(1.0f)
        }
    }

    fun playFailure(player: Player) {
        player.playSound(true) {
            type(BukkitSound.ENTITY_ITEM_BREAK)
            source(AdventureSound.Source.AMBIENT)
            pitch(1.0f)
        }
    }

    fun playHighscore(player: Player) {
        player.playSound(true) {
            type(BukkitSound.ENTITY_ENDER_DRAGON_GROWL)
            source(AdventureSound.Source.HOSTILE)
            pitch(1.0f)
        }
    }

    fun playInfo(player: Player) {
        player.playSound(true) {
            type(BukkitSound.ENTITY_VILLAGER_YES)
            source(AdventureSound.Source.NEUTRAL)
            pitch(1.0f)
        }
    }

    companion object {
        val INSTANCE = SoundService()
    }
}

val soundService get() = SoundService.INSTANCE