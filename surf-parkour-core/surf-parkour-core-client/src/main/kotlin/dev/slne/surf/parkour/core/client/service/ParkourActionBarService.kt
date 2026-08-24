package dev.slne.surf.parkour.core.client.service

import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.parkour.core.client.platform.ParkourPlatform
import dev.slne.surf.parkour.core.client.util.formattedDuration

/**
 * Shows everyone currently running a parkour how far they have come.
 *
 * The platform decides how often [sendUpdates] runs; [UPDATE_INTERVAL_MILLIS] is the interval the
 * action bar is written for.
 */
object ParkourActionBarService {

    /**
     * How long an action bar stays up before it is written again.
     */
    const val UPDATE_INTERVAL_MILLIS = 500L

    /**
     * Writes one action bar per player currently running a parkour.
     */
    fun sendUpdates() {
        val now = System.currentTimeMillis()

        ParkourService.parkours.forEach { parkour ->
            parkour.generators.values.forEach { generator ->
                val audience = ParkourPlatform.audience(generator.associatedPlayer)
                    ?: return@forEach

                audience.sendActionBar(buildText {
                    darkSpacer("»")
                    appendSpace()
                    variableKey("Sprünge:")
                    appendSpace()
                    variableValue(generator.currentIndex)
                    appendSpace()
                    spacer("|")
                    appendSpace()
                    variableKey("Zeit:")
                    appendSpace()
                    variableValue((now - generator.startTime).formattedDuration)
                    appendSpace()
                    darkSpacer("«")
                })
            }
        }
    }
}
