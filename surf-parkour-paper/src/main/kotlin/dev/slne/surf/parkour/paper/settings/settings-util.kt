package dev.slne.surf.parkour.paper.settings

import dev.slne.surf.surfapi.bukkit.api.extensions.pluginManager

fun hasSettingsApi() = pluginManager.isPluginEnabled("surf-settings-paper")