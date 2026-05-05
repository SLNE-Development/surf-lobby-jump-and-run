package dev.slne.surf.parkour.paper.settings

import dev.slne.surf.settings.api.surfSettingsApi
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object SettingsHook {

    fun Player.hasSoundsEnabled(): Boolean {
        return if (hasSettingsApi()) {
            surfSettingsApi.getPlayerSetting(this.uniqueId, "lobby_parkour_sound")?.getBoolean() ?: true
        } else {
            true
        }
    }
}