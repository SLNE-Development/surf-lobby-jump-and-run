package dev.slne.surf.parkour.core.client.service

import dev.slne.surf.api.core.messages.adventure.playSound
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound

object SoundService {
    private val chickenEgg = Key.key("entity.chicken.egg")
    private val itemBreak = Key.key("entity.item.break")

    fun playSuccess(audience: Audience) {
        audience.playSound(true) {
            type(chickenEgg)
            source(Sound.Source.NEUTRAL)
            pitch(1.0f)
        }
    }

    fun playFailure(audience: Audience) {
        audience.playSound(true) {
            type(itemBreak)
            source(Sound.Source.AMBIENT)
            pitch(1.0f)
        }
    }
}
